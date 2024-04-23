<template>
  <el-container>
    <!-- 侧边栏 -->
    <el-aside width="auto">
      <SideBar />
    </el-aside>

    <el-container :class="'main-container ' + isHide">
      <!-- 导航栏 -->
      <el-header height="84px" style="padding: 0">
        <NavBar :key="$route.fullPath" />
      </el-header>
      <!-- 主题内容栏 -->
      <el-main style="background: #f7f9fb">
        <div class="fade-transform-box">
          <transition name="fade-transform" mode="out-in">

            <!-- 路由的组件渲染处 -->
            <router-view :key="$route.fullPath" />

          </transition>
        </div>
      </el-main>

    </el-container>

  </el-container>
</template>

<script>
import NavBar from '@/layout/components/NavBar.vue'
import SideBar from '@/layout/components/SideBar.vue'
//默认导出，可以在其他组件中使用
export default {
  components: {
    NavBar,
    SideBar
  },
  computed: {
    isHide() {
      return this.$store.state.collapse ? 'hideSideBar' : ''
    }
  }
}
</script>

<style scoped>
.main-container {
  transition: margin-left 0.45s;
  margin-left: 210px;
  min-height: 100vh;
}

.hideSideBar {
  margin-left: 64px;
}

.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.5s ease 0s;
}

.fade-transform-enter {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

.fade-transform-box {
  position: relative;
  top: 0px;
  bottom: 0px;
  width: 100%;
  overflow: hidden;
}
</style>
