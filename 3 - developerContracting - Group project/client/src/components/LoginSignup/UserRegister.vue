<script>
import axios from 'axios';

export default {
  data() {
    return {
      email: '',
      password: '',
      dname: '',
      dsurname: '',
      country: '',
      errorMsg: '',
      date: '',
      description: '',
    };
  },
  methods: {
    register() {
      const path = 'http://localhost:5000/api/register-developer/';
      // eslint-disable-next-line
      axios.post(path, { DeveloperEmail: this.email, Password: this.password, Name: this.dname, Surname: this.dsurname, Country: this.country, DOB: this.date, Description: this.description })
        .then((res) => {
          this.$cookies.set('token', res.data.token);
          this.$store.commit('setJwtToken', res.data.token);
          // eslint-disable-next-line
          this.$store.commit('setDeveloperUserData', { DeveloperEmail: this.email, Password: this.password, Name: this.dname, Surname: this.dsurname, Country: this.country, DOB: this.date, Description: this.description });
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        });
      this.$router.push('/userlogin/');
    },
  },
};
</script>

<template>
  <div class="container-fluid  mt-5">
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
                  USER REGISTER
                </h3> <br>
                <form v-on:submit.prevent="register" autocomplete="on">

                <div class="scroll">

                <div class="form-group"> <label for="email">Name:
                <input type="text" class="form-control"
                id="email" placeholder="Enter name" v-model="dname" required>
                </label>
                </div>
                <br>

                <div class="form-group"> <label for="email">Surname:
                <input type="text" class="form-control"
                id="email" placeholder="Enter surname" v-model="dsurname" required>
                </label>
                </div>
                <br>

                <div class="form-group"> <label for="email">Country:
                <input type="text" class="form-control"
                id="email" placeholder="Enter country" v-model="country" required>
                </label>
                </div>
                <br>

                <div class="form-group"> <label for="email">Email Address:
                <input type="email" class="form-control"
                id="email" placeholder="Enter email" v-model="email" required>
                </label>
                </div>
                <br>

                <div class="form-group"> <label for="pwd">Password:
                <input type="password" class="form-control"
                id="pwd" placeholder="Enter password" v-model="password" required>
                </label>
                </div>
                <br>

                <!--<div class="form-group"> <label for="photo">User Avatar:
                <input type="file" accept=".jpg,.gif,.png" class="form-control"
                id="photo">
                </label>
                </div>
                <br>-->

                <div class="form-group"> <label for="describe">Describe yourself:
                <textarea type="text" class="form-control" v-model="description"
                id="describe" placeholder="Enter experiences" required/> </label> </div>

                <div class="form-group"> <label for="date">Birth date:
                <input type="date" class="form-control"
                id="date" v-model="date" required>
                </label>
                </div>
                <br>

                <button type="submit" name="submit" class="btn btn-primary">
                  Submit</button>
                <br> <br>

                <p>Have an account? <router-link to="/userlogin"> Login
                </router-link> </p>

                </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.bg-light-aqua {
  background-color: var(--light-aqua);
}
.btn-white {
  background-color: white;
  color: black;
}
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
    -webkit-box-shadow: inset 0 0 6px rgb(0, 0, 0);
}
</style>
