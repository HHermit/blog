import Layout from '@/layout/index.vue'
import router from '@/router'
import store from '@/store'
import axios from 'axios'
import Vue from 'vue'

// 根据当前用户id查询角色生成对应权限的菜单
export function generaMenu() {
  axios.get('/api/admin/user/menus').then(({ data }) => {
    if (data.flag) {
      let userMenus = data.data
      userMenus.forEach((item) => {
        if (item.icon != null) {
          item.icon = 'iconfont ' + item.icon
        }
        //如果组件名为Layout，则将组件名为layout 也就是'@/layout/index.vue'的实例存储进去
        if (item.component == 'Layout') {
          item.component = Layout
        }
        if (item.children && item.children.length > 0) {
          item.children.forEach((route) => {
            route.icon = 'iconfont ' + route.icon
            route.component = loadView(route.component)
          })
        }
      })
      store.commit('saveUserMenus', userMenus)
      // 动态添加路由到Vue-Router
      userMenus.forEach((item) => {
        router.addRoute(item)
      })
    } else {
      Vue.prototype.$message.error(data.message)
      router.push({ path: '/login' })
    }
  })
}

// 动态加载指定路径的视图组件。
export const loadView = (view) => {
  return (resolve) => require([`@/views${view}`], resolve)
}
