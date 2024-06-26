import { createI18n } from 'vue-i18n'
import cookies from 'js-cookie'

//加载本地语言文件
function loadLocaleMessages(): {
  [key: string]: { [key: string]: { [key: string]: string } }
} {
  const locales = require.context('../locales/languages', true, /[A-Za-z0-9-_,\s]+\.json$/i)
  const messages: {
    [key: string]: { [key: string]: { [key: string]: string } }
  } = {}
  locales.keys().forEach((key) => {
    const matched = key.match(/([A-Za-z0-9-_]+)\./i)
    if (matched && matched.length > 1) {
      const locale = matched[1]
      messages[locale] = locales(key)
    }
  })
  return messages
}

export const i18n = createI18n({
  locale: cookies.get('locale') ? String(cookies.get('locale')) : 'cn',
  fallbackLocale: cookies.get('locale') ? String(cookies.get('locale')) : 'cn',
  messages: loadLocaleMessages()
})
