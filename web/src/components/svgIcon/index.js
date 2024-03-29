import Vue from 'vue'
import SvgIcon from './index.vue'

// register globally
Vue.component('SvgIcon', SvgIcon)

const req = require.context('./svg', true, /\.svg$/)
const requireAll = requireContext => requireContext.keys().map(requireContext)
requireAll(req)