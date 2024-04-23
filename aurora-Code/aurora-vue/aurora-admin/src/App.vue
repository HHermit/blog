<template>
  <div id="app">
    <!-- 子页面渲染位置 -->
    <router-view />
  </div>
</template>

<script>
import { generaMenu } from '@/assets/js/menu'
export default {
  created() {
    //如果Vue实例state中有userInfo，则生成菜单
    if (this.$store.state.userInfo != null) {
      generaMenu()
    }
    this.axios.post('/api/report')
  }
}
</script>
