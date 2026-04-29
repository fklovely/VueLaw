import Vue from 'vue'
import Vuex from 'vuex'
import Cookies from 'js-cookie'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    token: Cookies.get('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}'),
    role: Cookies.get('role') || ''
  },
  getters: {
    token: state => state.token,
    userInfo: state => state.userInfo,
    role: state => state.role,
    isLoggedIn: state => !!state.token,
    isAdmin: state => state.role === 'admin',
    isLawyer: state => state.role === 'lawyer',
    isClient: state => state.role === 'client'
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token
      Cookies.set('token', token, { expires: 7 })
    },
    SET_USER_INFO(state, userInfo) {
      state.userInfo = userInfo
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    },
    SET_ROLE(state, role) {
      state.role = role
      Cookies.set('role', role, { expires: 7 })
    },
    LOGOUT(state) {
      state.token = ''
      state.userInfo = {}
      state.role = ''
      Cookies.remove('token')
      Cookies.remove('role')
      localStorage.removeItem('userInfo')
    }
  },
  actions: {
    login({ commit }, data) {
      commit('SET_TOKEN', data.token)
      commit('SET_ROLE', data.role)
      commit('SET_USER_INFO', {
        userId: data.userId,
        username: data.username,
        realName: data.realName,
        avatar: data.avatar,
        role: data.role
      })
    },
    logout({ commit }) {
      commit('LOGOUT')
    },
    updateUserInfo({ commit }, userInfo) {
      commit('SET_USER_INFO', userInfo)
    }
  }
})
