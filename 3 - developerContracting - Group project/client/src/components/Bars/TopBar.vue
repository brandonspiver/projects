<script>
export default {
  methods: {
    getEmail() {
      return this.$cookies.get('email');
    },
    logout() {
      this.$store.commit('logout');
      this.$router.push('/');
    },
  },
  computed: {
    isAuthenticated() {
      return this.$store.getters.isAuthenticated;
    },
  },
};
</script>

<template>
  <div class="gradient_background mb-3">
  <div class="container-fluid">
      <div class="row">
        <div class="hstack gap-3">
          <div>
            <router-link to="/home">
              <img alt="logo" src="../../../images/logos/jobits_logo_white.png">
            </router-link>
          </div>

          <router-link v-if="!isAuthenticated" to="/userlogin" role="button"
          class="shadow ms-auto btn btn-white btn-lg opacity-75 px-5 text-nowrap">
            Login | Signup
          </router-link>
          <router-link v-if="!this.$store.state.isDeveloper && isAuthenticated"
          to="/newcontract" role="button"
          class="shadow ms-auto btn btn-white btn-lg opacity-75 px-5 text-nowrap">
            Contracts
          </router-link>
          <button v-if="isAuthenticated" @click="logout"
          class="shadow btn btn-white btn-lg opacity-75 px-5 text-nowrap">
            Logout
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
img {
  display: block;
  max-width: 20%;
  height: auto;
}
:root {
  --aqua: #5787A2;
}
</style>

<style scoped>
.topbar {
}
.btn-aqua {
  background-color: var(--aqua);
  color: white;
}
.btn-white {
  background-color: white;
  color: black;
}
.gradient_background {
  background-image: url('../../../images/nav_gradient.png');
  background-repeat: no-repeat;
  background-size: cover;
}
</style>
