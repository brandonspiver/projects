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
      const path = 'http://localhost:5000/api/developer-profile-page/';
      // eslint-disable-next-line
      axios.get(path, { headers: { Authorization: `Bearer: ${this.$store.state.jwt}` } })
        .then((res) => {
          this.$store.commit('setDeveloperProfileData', res.data);
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        });
      this.$router.push('/developerprofile/');
    },
    companyProfile() {
      const path = 'http://localhost:5000/api/company-profile-page/';
      // eslint-disable-next-line
      axios.get(path, { headers: { Authorization: `Bearer: ${this.$store.state.jwt}` } })
        .then((res) => {
          this.$store.commit('setCompanyProfileData', res.data);
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
    <div class="midbar">
      <div class="row">
        <div class="gradient_background">
          <div v-if="this.$store.state.isDeveloper && isAuthenticated">
            <div class="hstack gap-3 mt-3 mx-5">
              <a href="#" @click="developerProfile" class="text-decoration-none">
                <p>Profile Page</p>
              </a>
              <router-link to="/editprofile" class="text-decoration-none">
                <p>Edit Profile Page</p>
              </router-link>
            </div>
          </div>
          <div v-if="!this.$store.state.isDeveloper && isAuthenticated">
            <div class="hstack gap-3 mt-3 mx-5">
              <a href="#" @click="companyProfile" class="text-decoration-none">
                <p>Company Page</p>
              </a>
              <router-link to="/editprofile" class="text-decoration-none">
                <p>Edit Company Page</p>
              </router-link>
              <router-link to="/editprofile" class="text-decoration-none">
                <p>Current Contracts</p>
              </router-link>
            </div>
          </div>
          <div class="hstack gap-3 mb-3 mx-5 mt-3">
            <input v-model="searchText"
            @change="$emit('searchText', searchText)" class="form-control" type="text"
            placeholder="Search and press enter..."
            aria-label="Search and press enter..." style="width: 50%;">
            <img alt="logo" src="../../../images/icons/search.png">
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
img {
  display: block;
  max-width: 50%;
  height: auto;
}
</style>

<style scoped>
.midbar {
}
p {
  color: white;
}
.gradient_background {
  background-image: url('../../../images/nav_gradient.png');
  background-repeat: no-repeat;
  background-size: cover;
}
</style>
