import Vue from 'vue';
import Router from 'vue-router';
import store from '@/store';
import ViewSelector from '../components/Content/ViewSelector.vue';
import CompanyLogin from '../components/LoginSignup/CompanyLogin.vue';
import UserLogin from '../components/LoginSignup/UserLogin.vue';
import CompanyRegister from '../components/LoginSignup/CompanyRegister.vue';
import UserRegister from '../components/LoginSignup/UserRegister.vue';
import ViewContract from '../components/Content/ViewContract.vue';
import DeveloperProfile from '../components/Profile/DeveloperProfile.vue';
import CompanyProfile from '../components/Profile/CompanyProfile.vue';
import NewContract from '../components/Contracts/NewContract.vue';
import DeleteAccount from '../components/Profile/DeleteAccount.vue';
import ThankYouForLeaving from '../components/Profile/ThankYouForLeaving.vue';
import AccountBlacklisted from '../components/Profile/AccountBlacklisted.vue';

Vue.use(Router);

Vue.config.productionTip = false;

export default new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      redirect: '/home',
    },
    {
      path: '/home',
      name: 'ViewSelector',
      component: ViewSelector,
    },
    {
      path: '/companylogin',
      name: 'CompanyLogin',
      component: CompanyLogin,
    },
    {
      path: '/userlogin',
      name: 'UserLogin',
      component: UserLogin,
    },
    {
      path: '/companyregister',
      name: 'CompanyRegister',
      component: CompanyRegister,
    },
    {
      path: '/userregister',
      name: 'UserRegister',
      component: UserRegister,
    },
    {
      path: '/viewcontract',
      name: 'ViewContract',
      component: ViewContract,
      props: true,
    },
    {
      path: '/developerprofile',
      name: 'DeveloperProfile',
      component: DeveloperProfile,
      beforeEnter(to, from, next) {
        if (!store.getters.isAuthenticated) {
          next('/userlogin');
        } else {
          next();
        }
      },
    },
    {
      path: '/companyprofile',
      name: 'CompanyProfile',
      component: CompanyProfile,
    },
    {
      path: '/newcontract',
      name: 'NewContract',
      component: NewContract,
    },
    {
      path: '/deleteaccount',
      name: 'DeleteAccount',
      component: DeleteAccount,
    },
    {
      path: '/thankyouforleaving',
      name: 'ThankYouForLeaving',
      component: ThankYouForLeaving,
    },
    {
      path: '/accountblacklisted',
      name: 'AccountBlacklisted',
      component: AccountBlacklisted,
    },
  ],
});
