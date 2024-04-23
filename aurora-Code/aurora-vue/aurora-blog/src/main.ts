import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './router/guard'
import '@/styles/index.scss'
import 'normalize.css/normalize.css'
import { createPinia } from 'pinia'
import { i18n } from './locales'
import VueClickAway from 'vue3-click-away'
import lazyPlugin from 'vue3-lazy'
import { registerSvgIcon } from '@/icons'
import { registerObSkeleton } from '@/components/LoadingSkeleton'
import 'prismjs/themes/prism.css'
import 'prismjs'
import 'element-plus/theme-chalk/index.css'
import { components, plugins } from './plugins/element-plus'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import infiniteScroll from 'vue3-infinite-scroll-better'
import v3ImgPreview from 'v3-img-preview'
import 'mavon-editor/dist/css/index.css'
import api from './api/api'
import axios from 'axios'
import { useUserStore } from '@/stores/user'

// 初始化Pinia状态管理工具并使用持久化插件:将Pinia store中的状态持久化到浏览器的localStorage或sessionStorage中
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

// 创建并配置Vue应用实例
export const app = createApp(App)
  .use(router) // 使用Vue Router进行路由管理
  .use(pinia) // 使用Pinia进行状态管理
  .use(i18n) // 使用国际化插件
  .use(VueClickAway) // 使用点击远离插件
  .use(infiniteScroll) // 使用无限滚动插件
  .use(v3ImgPreview, {}) // 使用图片预览插件
  .use(lazyPlugin, { // 使用延迟加载插件
    loading: require('@/assets/default-cover.jpg'), // 加载中的占位图
    error: require('@/assets/default-cover.jpg') // 加载失败的占位图
  })

// 获取用户状态存储实例
const userStore = useUserStore()

// 在axios请求拦截器中设置授权头
axios.interceptors.request.use((config: any) => {
  config.headers['Authorization'] = 'Bearer ' + sessionStorage.getItem('token')
  return config
})

// 获取Vue实例的全局属性，并设置axios响应拦截器
const proxy = app.config.globalProperties
axios.interceptors.response.use(
  (response) => {
    // 根据响应数据的flag字段决定是否正常返回响应
    if (response.data.flag) {
      return response
    }
    // 根据响应数据的code字段进行错误处理
    switch (response.data.code) {
      case 50000: {
        proxy.$notify({
          title: 'Error',
          message: '系统异常',
          type: 'error'
        })
        break
      }
      case 40001: {
        proxy.$notify({
          title: 'Error',
          message: '用户未登录',
          type: 'error'
        })
        // 如果已登录过，则清除用户信息和token，并从sessionStorage移除token
        if (userStore.userInfo !== '') {
          userStore.userInfo = ''
          userStore.token = ''
          userStore.accessArticles = []
          sessionStorage.removeItem('token')
        }
        break
      }
      default: {
        proxy.$notify({
          title: 'Error',
          message: response.data.message,
          type: 'error'
        })
        break
      }
    }
    return response
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 注册Element中的组件
components.forEach((component) => {
  app.component(component.name, component)
})

// 注册Element中的插件
plugins.forEach((plugin) => {
  app.use(plugin)
})

// 为根组件注册SVG图标和骨架屏（在页面未加载出来时，向用户展示的页面）
registerSvgIcon(app)
registerObSkeleton(app)

// 将应用实例挂载到HTML页面中的根元素
app.mount('#app')

// 打印作者和联系方式信息
console.log('%c 网站作者:啊哈14', 'color:#bada55')
console.log('%c qq:3347778009', 'color:#bada55')

// 调用API报告函数
api.report()