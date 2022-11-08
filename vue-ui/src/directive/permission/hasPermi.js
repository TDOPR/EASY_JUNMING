import store from '@/store/index'

export default {
  inserted(el, binding, vnode) {
    const { value } = binding
    const all_permission = "*:*:*";
    const permissions = store.state.permissionList || []
    if (value && value!='') {
      const hasPermissions = permissions.includes(all_permission) || permissions.includes(value)
      if (!hasPermissions) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else {
      throw new Error(`请设置操作权限标签值`)
    }
  }
}
