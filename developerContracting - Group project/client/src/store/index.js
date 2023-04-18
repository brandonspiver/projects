import Vue from 'vue';
import Vuex from 'vuex';
import createPersistedState from 'vuex-persistedstate';
import { isValidJwt } from '@/utils';

Vue.use(Vuex);

const state = {
  isDeveloper: false,
  userProfile: {},
  userData: {},
  otherProfile: {},
  contracts: {},
  jwt: '',
};

const actions = {
};

const mutations = {
  // eslint-disable-next-line
  setDeveloperUserData(state, data) {
    state.userData = data;
    state.isDeveloper = true;
  },
  // eslint-disable-next-line
  setCompanyUserData(state, data) {
    state.userData = data;
    state.isDeveloper = false;
  },
  // eslint-disable-next-line
  setOtherProfileData(state, data) {
    state.otherProfile = data;
  },
  // eslint-disable-next-line
  setJwtToken(state, token) {
    state.jwt = token;
  },
  // eslint-disable-next-line
  logout(state) {
    state.jwt = '';
    state.userData = {};
    state.userProfile = {};
  },
  // eslint-disable-next-line
  setDeveloperProfileData(state, data) {
    state.userProfile = data;
  },
  // eslint-disable-next-line
  setCompanyProfileData(state, data) {
    state.userProfile = data;
  },
};

const getters = {
  // eslint-disable-next-line
  isAuthenticated(state) {
    return isValidJwt(state.jwt);
  },
};

const dataState = createPersistedState({
  paths: ['isDeveloper', 'user', 'userData', 'jwt', 'userProfile'],
});

const store = new Vuex.Store({
  state,
  actions,
  mutations,
  getters,
  plugins: [dataState],
});

export default store;
