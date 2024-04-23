const { defineConfig } = require('@vue/cli-service')
// require  ：引入了Node.js的内置模块"path"  
const path = require('path')
//将传入的dir与当前执行脚本所在的绝对路径__dirname进行拼接，返回拼接后的完整绝对路径
function resolve(dir) {
  return path.join(__dirname, dir)
}

module.exports = defineConfig({
  transpileDependencies: true,
  productionSourceMap: false,

  //配置开发服务器的各项参数
  devServer: {
    //设置dev环境的启动端口
    port: 8081,
    proxy: {
      '/api': {
        target: 'https://www.linhaojun.top/api',  //本地调试，不使用服务，使用域名的情况
        // target: 'https://localhost:8080/api',  因为使用的是http服务
        //配置服务端的地址
        // target: 'http://localhost:8080',//不需要夹/api，因为没开nginx代理，不需要反向再转向nginx 
        // target: 'http://www.aha14.top/api',//虚拟机配置 
        changeOrigin: true,  //服务器收到的请求头中的host为：服务器相同的host端口 不再有跨域
        // 配置路径重写，将请求路径中的/api替换为空字符串。
        pathRewrite: {
          '^/api': ''
        }
      }
    }
  },
  //使用简短的别名代替较长的真实路径  eg：@/components/something.vue
  chainWebpack: (config) => {
    config.resolve.alias.set('@', resolve('src'))
  }
})
