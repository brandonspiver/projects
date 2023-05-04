<script>
import axios from 'axios';

export default {
  data() {
    return {
      cname: '',
      password: '',
      errorMsg: '',
    };
  },
  methods: {
    authenticate() {
      const path = 'http://localhost:5000/api/login-company/';
      axios.post(path, { CompanyName: this.cname, Password: this.password })
        .then((res) => {
          this.$cookies.set('token', res.data.token);
          this.$store.commit('setJwtToken', res.data.token);
          this.$store.commit('setCompanyUserData', { CompanyName: this.cname, CompanyID: res.data.CompanyID });
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        });
      this.$router.push('/');
    },
  },
};
</script>

<template>
  <div class="container-fluid mt-5">
    <div class="row">
      <div class="col-5">
        <div class="d-flex justify-content-end">
          <div class="card bg-light-aqua p-3 mt-2 border-0">
            <router-link to="/userlogin" role="button"
            class="btn btn-white btn-lg px-5 text-nowrap">
              User
            </router-link>

            <router-link to="/companylogin" role="button"
            class="btn btn-white btn-lg px-5 text-nowrap mt-2">
              Company
            </router-link>
          </div>
        </div>
      </div>
      <div class="col-7">
        <div class="card bg-light-aqua p-3 mt-2 border-0 w-50">
          <div class="card-body">
            <div class="card bg-white text-black p-3 mt-2 border-0">
              <div class="card-body">
                <h3 class="card-title text-center">
                  COMPANY LOGIN
                </h3> <br>
                <form v-on:submit.prevent="authenticate" autocomplete="on">

                <div class="form-group"> <label for="usr">Company Name:
                <input type="text" class="form-control" v-model="cname"
                id="usr" placeholder="Enter name" required>
                </label>
                </div>
                <br>

                <div class="form-group"> <label for="pwd">Password:
                <input type="password" class="form-control" v-model="password"
                id="pwd" placeholder="Enter password" required>
                </label>
                </div>
                <br>

                <button type="submit" name="submit" class="btn btn-primary">Submit</button>
                <br> <br>

                <p>Don't have an account? <router-link to="/companyregister"> Register
                </router-link> </p>

                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
:root {
  --light-aqua: #94C1C6;
}
.scroll {
    overflow-y: scroll;
    height: 50vh;
    width: 100%;
    margin: 0 0 10px 0;
}
.scroll::-webkit-scrollbar {
    width:5px;
}
.scroll::-webkit-scrollbar-track {
    -webkit-box-shadow:inset 0 0 6px rgba(0,0,0,0.3);
    border-radius:5px;
}
.scroll::-webkit-scrollbar-thumb {
    border-radius:5px;
    -webkit-box-shadow: inset 0 0 6px red;
}
</style>

<style scoped>
.bg-light-aqua {
  background-color: var(--light-aqua);
}
.btn-white {
  background-color: white;
  color: black;
}
</style>
