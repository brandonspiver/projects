<script>
import axios from 'axios';

export default {
  data() {
    return {
      cname: '',
      password: '',
      gen: '',
      address: '',
      year: '',
      errorMsg: '',
      email: '',
      phone: '',
    };
  },
  methods: {
    register() {
      const path = 'http://localhost:5000/api/register-company/';
      // eslint-disable-next-line
      axios.post(path,{ CompanyName: this.cname, Password: this.password, GeneralIndustry: this.gen, CompanyAddress: this.address, YearEstablished: this.year, CompanyEmail: this.email, CompanyPhone: this.phone })
        .then((res) => {
          this.$cookies.set('token', res.data.token);
          this.$store.commit('setJwtToken', res.data.token);
          this.$store.commit('setCompanyUserData', { CompanyName: this.cname });
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        });
      this.$router.push('/companylogin/');
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
                  COMPANY REGISTER
                </h3> <br>
                <form v-on:submit.prevent="register" autocomplete="on">

                <div class="scroll">

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

                <div class="form-group"> <label for="email">Admin Email:
                <input type="email" class="form-control"
                id="email" placeholder="Enter admin email" v-model="email" required>
                </label>
                </div>
                <br>

                <div class="form-group"> <label for="number">Phone Number:
                <input type="text" class="form-control"
                id="number" placeholder="Enter phone number" v-model="phone" required>
                </label>
                </div>
                <br>

                <!--<div class="form-group"> <label for="photo">Company Logo:
                <input type="file" accept=".jpg,.gif,.png" class="form-control" id="photo">
                </label>
                </div>
                <br>-->

                <div class="form-group"> <label for="industry">General Industry:
                <input type="text" class="form-control" v-model="gen"
                id="industry" placeholder="Enter industry" required>
                </label>
                </div>
                <br>

                <div class="form-group"> <label for="usr">Company Address:
                <input type="text" class="form-control" v-model="address"
                id="usr" placeholder="Enter address" required>
                </label>
                </div>
                <br>

                <div class="form-group"> <label for="usr">Time in business:
                <input type="text" class="form-control" v-model="year"
                id="usr" placeholder="Enter number of years" required>
                </label>
                </div>
                <br>

                <button type="submit" name="submit" class="btn btn-primary">Submit</button>
                <br> <br>

                <p>Have an account? <router-link to="/companylogin"> Login
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
    -webkit-box-shadow: inset 0 0 6px rgb(0, 0, 0);;
}
</style>
