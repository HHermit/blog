import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from 'vuex-persistedstate'

Vue.use(Vuex)

export default new Vuex.Store({
  //状态
  state: {
    collapse: false, //侧边栏是否折叠
    tabList: [{ name: '首页', path: '/' }],
    userInfo: null,
    userMenus: [],
    pageState: {
      articleList: 1,
      category: 1,
      tag: 1,
      comment: 1,
      talkList: 1,
      user: 1,
      online: 1,
      role: 1,
      quartz: 1,
      friendLink: 1,
      operationLog: 1,
      exceptionLog: 1,
      quartzLog: {
        jobId: -1,
        current: 1
      },
      photo: {
        albumId: -1,
        current: 1
      }
    }
  },
  //改变上述状态的方法 只能通过 mutations 来改变 state
  //使用store.commit方法触发mutations改变state:  store.commit('saveTab');
  mutations: {
    saveTab(state, tab) {
      if (state.tabList.findIndex((item) => item.path === tab.path) == -1) {
        state.tabList.push({ name: tab.name, path: tab.path })
      }
    },
    removeTab(state, tab) {
      var index = state.tabList.findIndex((item) => item.name === tab.name)
      state.tabList.splice(index, 1)
    },
    resetTab(state) {
      state.tabList = [{ name: '首页', path: '/' }]
    },
    trigger(state) {
      state.collapse = !state.collapse
    },
    //设置token到sessionStorage
    login(state, user) {
      sessionStorage.setItem('token', user.token)
      state.userInfo = user
    },
    saveUserMenus(state, userMenus) {
      state.userMenus = userMenus
    },
    logout(state) {
      state.userInfo = null
      sessionStorage.removeItem('token')
      state.userMenus = []
    },
    updateAvatar(state, avatar) {
      state.userInfo.avatar = avatar
    },
    updateUserInfo(state, user) {
      state.userInfo.nickname = user.nickname
      state.userInfo.intro = user.intro
      state.userInfo.webSite = user.webSite
    },
    updateArticleListPageState(state, current) {
      state.pageState.articleList = current
    },
    updateCategoryPageState(state, current) {
      state.pageState.category = current
    },
    updateTagPageState(state, current) {
      state.pageState.tag = current
    },
    updateCommentPageState(state, current) {
      state.pageState.comment = current
    },
    updateTalkListPageState(state, current) {
      state.pageState.talkList = current
    },
    updateUserPageState(state, current) {
      state.pageState.user = current
    },
    updateOnlinePageState(state, current) {
      state.pageState.online = current
    },
    updateRolePageState(state, current) {
      state.pageState.role = current
    },
    updateQuartzPageState(state, current) {
      state.pageState.quartz = current
    },
    updateFriendLinkPageState(state, current) {
      state.pageState.friendLink = current
    },
    updateOperationLogPageState(state, current) {
      state.pageState.operationLog = current
    },
    updateExceptionLogPageState(state, current) {
      state.pageState.exceptionLog = current
    },
    updateQuartzLogPageState(state, quartzLog) {
      state.pageState.quartzLog.jobId = quartzLog.jobId
      state.pageState.quartzLog.current = quartzLog.current
    },
    updatePhotoPageState(state, photo) {
      state.pageState.photo.albumId = photo.albumId
      state.pageState.photo.current = photo.current
    }
  },
  plugins: [
    //在状态发生变化时自动将其保存到sessionStorage中。
    createPersistedState({
      storage: window.sessionStorage
    })
  ]
})
