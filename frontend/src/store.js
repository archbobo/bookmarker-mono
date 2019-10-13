import Vue from 'vue'
import Vuex from 'vuex'
import axios from './axios-config'

Vue.use(Vuex)

const state = {
    user: JSON.parse(localStorage.getItem('currentUser') || '{}'),
    auth_token: {
        access_token: localStorage.getItem('access_token')
    },
    bookmarks: [],
    selectedTag: { bookmarks: [] },
    tags: []
}

const mutations = {
    setTags (state, tags) {
        state.tags = tags
    },
    setBookmarks (state, bookmarks) {
        state.bookmarks = bookmarks
    },
    setSelectedTag (state, selectedTag) {
        state.selectedTag = selectedTag
    },
    setCurrentUser (state, currentUser) {
        state.user = currentUser
        localStorage.setItem('currentUser', JSON.stringify(currentUser))
    },
    setAuth (state, authResponse) {
        state.auth_token = authResponse
        localStorage.setItem('access_token', authResponse.access_token)
    },
    clearAuth (state) {
        state.auth_token = {}
        localStorage.removeItem('access_token')
        localStorage.removeItem('currentUser')
    }
}

const actions = {
    async fetchTags ({ commit }) {
    let tags = (await axios.get('tags'))
    commit('setTags', tags)
},

async fetchBookmarks ({ commit }) {
    let bookmarks = (await axios.get('bookmarks'))
    commit('setBookmarks', bookmarks)
},

async fetchBookmarksByTag ({ commit }, tag) {
    let selectedTag = (await axios.get(`bookmarks/tagged/${tag}`))
    commit('setSelectedTag', selectedTag)
},

async fetchBookmarksByUser ({ commit }, userId) {
    return axios.get(`bookmarks?userId=${userId}`)
},

async createBookmark ({ commit, state }, bookmark) {
    return axios.post('bookmarks', bookmark)
},

async login ({ commit, state }, credentials) {
    let data = await axios.post('auth/login', credentials)
    commit('setAuth', data)
},

async fetchUserProfile ({ commit }, userId) {
    return axios.get(`users/${userId}`)
},

async fetchCurrentUser ({ commit }) {
    let currentUser = (await axios.get('me'))
    commit('setCurrentUser', currentUser)
},

async register ({ commit, state }, user) {
    return axios.post('users', user)
},

logout ({ commit }) {
    commit('clearAuth')
}

}

const getters = {
    currentUser: state => () => {
    const user = localStorage.getItem('currentUser') || '{}'
    return JSON.parse(user)
}
}

export default new Vuex.Store({
    state: state,
    mutations: mutations,
    actions: actions,
    getters: getters
})
