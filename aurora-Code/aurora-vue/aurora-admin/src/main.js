import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import './assets/css/index.css'
import './assets/css/iconfont.css'
import config from './assets/js/config'
import axios from 'axios'
import VueAxios from 'vue-axios'
import ECharts from 'vue-echarts'
import 'echarts/lib/chart/line'
import 'echarts/lib/chart/pie'
import 'echarts/lib/chart/bar'
import 'echarts/lib/chart/map'
import 'echarts/lib/component/tooltip'
import 'echarts/lib/component/legend'
import 'echarts/lib/component/title'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import mavonEditor from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
import VueCalendarHeatmap from 'vue-calendar-heatmap'
import tagCloud from './components/tag-cloud'
import dayjs from 'dayjs'
import Md_Katex from '@iktakahiro/markdown-it-katex'
import mermaidPlugin from "@agoose77/markdown-it-mermaid";

// 关闭 Vue 生产环境提示信息
Vue.config.productionTip = false

// 向 Vue 全局原型链注入 `config` 对象，以便在组件内直接访问
Vue.prototype.config = config

// 引入并注册第三方插件：

// 1. mavonEditor：Markdown 编辑器插件
Vue.use(mavonEditor)

// 2. ElementUI：基于 Vue 的桌面端 UI 组件库
Vue.use(ElementUI)

// 3. tagCloud：标签云组件
Vue.use(tagCloud)

// 4. VueCalendarHeatmap：日历热力图组件
Vue.use(VueCalendarHeatmap)

// 5. VueAxios：集成 axios，并将其作为 Vue 的 HTTP 请求工具
Vue.use(VueAxios, axios)

// 注册 ECharts 图表库为名为 "v-chart" 的全局 Vue 组件
Vue.component('v-chart', ECharts)

// 将 dayjs 日期处理库挂载到 Vue 实例的原型链上，便于在组件内使用 `$moment` 访问
Vue.prototype.$moment = dayjs

// 配置 mavonEditor 的 Markdown 处理引擎，添加 Katex 数学公式支持与 mermaid 图表插件
mavonEditor.markdownIt.set({}).use(Md_Katex).use(mermaidPlugin);

// 定义 date 格式化全局过滤器，用于格式化传入的时间戳或 Date 对象
// 参数说明：
// - value: 待格式化的日期/时间值（默认接受时间戳或 Date 对象）
// - formatStr (可选): 输出的日期格式，默认为 'YYYY-MM-DD'
Vue.filter('date', function (value, formatStr = 'YYYY-MM-DD') {
  return dayjs(value).format(formatStr)
})

// 定义 dateTime（日期时间）格式化全局过滤器，用于格式化传入的时间戳或 Date 对象
// 参数说明：
// - value: 待格式化的日期/时间值（默认接受时间戳或 Date 对象）
// - formatStr (可选): 输出的日期时间格式，默认为 'YYYY-MM-DD HH:mm:ss'
Vue.filter('dateTime', function (value, formatStr = 'YYYY-MM-DD HH:mm:ss') {
  return dayjs(value).format(formatStr)
})

// 配置 NProgress 进度条样式与行为
NProgress.configure({
  easing: 'ease', // 动画缓动方式
  speed: 500, // 动画完成所需毫秒数
  showSpinner: false, // 是否显示加载指示器
  trickleSpeed: 200, // 自动递增间隔（微调模式）毫秒数
  minimum: 0.3 // 最小进度条高度占比
})

// 路由守卫：在页面跳转前后控制 NProgress 加载进度条显示与隐藏

// 页面跳转前执行的操作
router.beforeEach((to, from, next) => {
  NProgress.start() // 开始显示加载进度条

  // 根据当前路径及用户登录状态决定是否允许跳转
  if (to.path === '/login') {
    next() // 直接允许跳转至登录页
  } else if (!store.state.userInfo) { // 用户未登录
    next({ path: '/login' }) // 强制跳转至登录页
  } else {
    next() // 允许跳转至其他已授权页面
  }
})

// 页面跳转后执行的操作
router.afterEach(() => {
  NProgress.done() // 结束加载进度条显示
})

// 配置 axios 请求与响应拦截器

// 请求拦截器：在发送请求前设置 Authorization 头部信息
axios.interceptors.request.use((config) => {
  const token = sessionStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}` // 添加一个键值对：Authorization ： Bearer  <token>
  }
  return config
})

// 响应拦截器：处理 API 返回的数据，根据错误码展示消息提示并可能进行页面跳转
axios.interceptors.response.use(
  (response) => {
    // 根据响应数据中的 code 字段判断并处理不同情况
    switch (response.data.code) {
      case 40001: // 登录过期或无效
        Vue.prototype.$message({ // 显示错误消息提示
          type: 'error',
          message: response.data.message
        })
        router.push({ path: '/login' }) // 跳转至登录页   编程式路由跳转
        break

      case 50000: // 服务器内部错误
        Vue.prototype.$message({ // 显示错误消息提示
          type: 'error',
          message: response.data.message
        })
        break

      default:
        return response // 其他情况直接返回原始响应对象
    }
  },
  (error) => {
    // 若发生网络异常或其他错误，返回 Promise.reject 以保持原生错误处理流程
    return Promise.reject(error)
  }
)

// 初始化 Vue 应用程序实例并挂载到 DOM 中
//新建一个Vue实例 
new Vue({
  router, //挂载路由
  store, //挂载store（状态管理）
  render: (h) => h(App)  // 定义渲染函数，指定根组件为 App.vue
}).$mount('#app')   //将Vue实例挂载到id为app的div上（index.html文件）
