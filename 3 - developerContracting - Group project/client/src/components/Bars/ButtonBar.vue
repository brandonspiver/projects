<script>
import axios from 'axios';

export default {
  data() {
    return {
      searchText: '',
    };
  },
  methods: {
    isUser() {
      return true;
    },
    developerProfile() {
      const path = 'http://localhost:5000/api/get-developer/';
      // eslint-disable-next-line
      axios.post(path, { DeveloperID: this.$store.state.userData.DeveloperID })
        .then((res) => {
          this.$store.commit('setOtherProfileData', res.data);
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        });
      this.$router.push('/developerprofile/');
    },
    companyProfile() {
      const path = 'http://localhost:5000/api/get-company/';
      // eslint-disable-next-line
      axios.post(path, { CompanyID: this.$store.state.userData.CompanyID })
        .then((res) => {
          this.$store.commit('setOtherProfileData', res.data);
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        });
      this.$router.push('/companyprofile/');
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
<div class="container-fluid">
  <div class="hstack gap-3">
    <div v-if="this.$store.state.isDeveloper || !isAuthenticated">
      <a href="#" @click="$router.push('/home')" class="text-decoration-none">
      <button class="shadow btn btn-light" style="width: 50vw">
        <span style="font-size: x-large;">Available Jobs</span>
      </button>
      </a>
    </div>
    <div v-else>
      <a href="#" @click="$router.push('/home')" class="text-decoration-none">
      <button class="shadow btn btn-light" style="width: 50vw">
        <span style="font-size: x-large;">Available Employees</span>
      </button>
      </a>
    </div>
    <div v-if="this.$store.state.isDeveloper || !isAuthenticated">
      <a href="#" @click="developerProfile" class="text-decoration-none">
      <button class="shadow btn btn-cyan">
        <span style="font-size: x-large;">My Profile</span>
      </button>
      </a>
    </div>
    <div v-else>
      <a href="#" @click="companyProfile" class="text-decoration-none">
      <button class="shadow btn btn-cyan">
        <span style="font-size: x-large;">Company Profile</span>
      </button>
      </a>
    </div>
  </div>
</div>
</template>

<style scoped>
.btn-cyan {
  background-color: #3FB5BC;
  color: white;
  width: 47vw;
}
</style>
