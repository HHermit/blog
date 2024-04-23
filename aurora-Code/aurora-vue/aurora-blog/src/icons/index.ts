import SvgIcon from '@/components/SvgIcon/index.vue' // svg component
import { App } from 'vue'

// register globally ：用来加载svg文件
export const registerSvgIcon = (app: App): void => {
  app.component('svg-icon', SvgIcon)
  const req = require.context('./svg', false, /\.svg$/)
  // eslint-disable-next-line
  const requireAll = (requireContext: any) =>
    requireContext.keys().map(requireContext)
  requireAll(req)
}
