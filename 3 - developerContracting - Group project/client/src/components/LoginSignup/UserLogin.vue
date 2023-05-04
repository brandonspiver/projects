<script>
import axios from 'axios';

export default {
  data() {
    return {
      email: '',
      password: '',
      errorMsg: '',
    };
  },
  methods: {
    authenticate() {
      const path = 'http://localhost:5000/api/login-developer/';
      axios.post(path, { DeveloperEmail: this.email, Password: this.password })
        .then((res) => {
          this.$cookies.set('token', res.data.token);
          this.$store.commit('setJwtToken', res.data.token);
          this.$store.commit('setDeveloperUserData', { DeveloperEmail: this.email, DeveloperID: res.data.DeveloperID });
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
                  USER LOGIN
                </h3> <br>
                <form v-on:submit.prevent="authenticate" autocomplete="on">

                <div class="form-group"> <label for="email">Email Address:
                <input type="email" class="form-control"
                id="email" v-model="email" placeholder="Enter email" required>
                </label>
                </div>
                <br>

                <div class="form-group"> <label for="pwd">Password:
                <input type="password" class="form-control"
                id="pwd" v-model="password" placeholder="Enter password" required>
                </label>
                </div>
                <br>

                <button type="submit" name="submit" value="Login"
                class="btn btn-primary">Submit</button>
                <br> <br>

                <p>Don't have an account? <router-link to="/userregister"> Register
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
