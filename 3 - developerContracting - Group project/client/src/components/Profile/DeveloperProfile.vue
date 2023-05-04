<script>
import axios from 'axios';
import ButtonBar from '@/components/Bars/ButtonBar.vue';

export default {
  created() {
    this.getContract();
    this.getApplications();
  },
  beforeMount() {
    this.getContract();
    this.getApplications();
  },
  components: { ButtonBar },
  data() {
    return {
      profile: this.$store.state.otherProfile,
      applications: {},
      constractsx: {},
      companies: {},
    };
  },
  methods: {
    getCompanyName(id) {
      let i = 0;
      for (i = 0; i < this.companies.length; i += 1) {
        if (this.companies[i].CompanyID === id) {
          return this.companies[i].CompanyName;
        }
      }
      return 0;
    },
    otherCompanyProfile(id) {
      const path = 'http://localhost:5000/api/get-company/';
      // eslint-disable-next-line
      axios.post(path, { CompanyID: id })
        .then((res) => {
          this.$store.commit('setOtherProfileData', res.data);
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        });
      this.$router.push('/companyprofile/');
    },
    getApplications() {
      const path = 'http://localhost:5000/api/applications/';
      // eslint-disable-next-line
      axios.get(path)
        .then((res) => {
          this.applications = res.data.applications;
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        });
    },
    getContract() {
      let path = 'http://localhost:5000/api/contracts';
      axios.get(path)
        .then((res) => {
          this.contractsx = res.data.contracts;
          this.$store.state.contracts = res.data.contracts;
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        });
      path = 'http://localhost:5000/api/companies';
      axios.get(path)
        .then((res) => {
          this.companies = res.data.companies;
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        });
    },
  },
};
</script>

<template>
  <div class="container-fluid">
    <ButtonBar />
    <div class="row">
    <div class="col-6">
    <div class="card bg-white text-black p-3 mt-3 border-0 mx-2">
      <div class="card-body">
        <h1 class="card-title">
          Basic Info
        </h1>
        <br>
        <div class="card-text">

          <div class="row">
            <div class="col-5">
              <h4 style="color: gray;">NAME:</h4>
            </div>
            <div class="col">
              <h4>{{ profile.Name }} </h4>
            </div>
          </div>

          <hr style="opacity: 20%;">

          <div class="row">
            <div class="col-5">
              <h4 style="color: gray;">SURNAME:</h4>
            </div>
            <div class="col">
              <h4>{{ profile.Surname }} </h4>
            </div>
          </div>

          <hr style="opacity: 20%;">

          <div class="row">
            <div class="col-5">
              <h4 style="color: gray;">BIRTH DATE:</h4>
            </div>
            <div class="col">
              <h4>{{ profile.DOB.split(" ")[0] }} {{ profile.DOB.split(" ")[1] }}
                 {{ profile.DOB.split(" ")[2] }} {{ profile.DOB.split(" ")[3] }}
              </h4>
            </div>
          </div>

          <hr style="opacity: 20%;">

          <div class="row">
            <div class="col-5">
              <h4 style="color: gray;">COUNTRY:</h4>
            </div>
            <div class="col">
              <h4>{{ profile.Country }} </h4>
            </div>
          </div>

        </div>
      </div>
    </div>

    <div class="card bg-white text-black p-3 mt-2 border-0 mx-2">
      <div class="card-body">
        <h1 class="card-title">
          Contact Info
        </h1>
        <br>
        <div class="card-text">

          <div class="row">
            <div class="col-5">
              <h4 style="color: gray;">EMAIL:</h4>
            </div>
            <div class="col">
              <h4>{{ profile.DeveloperEmail }} </h4>
            </div>
          </div>

        </div>
      </div>
    </div>

    </div>

    <div class="col-6 mt-3">
    <div class="rounded" style="background-color: rgba(255, 255, 255, 0.25); width: 100%;
    height: 65vh;">
      <div class="scroll container-fluid pt-3">

        <div v-for="(contract, index) in $store.state.contracts" :key="index">
          <div v-for="(application, index) in applications" :key="index">
            <div v-if="application.ContractID == contract.ContractID
              && application.DeveloperID == profile.DeveloperID">

              <button class="btn btn-light mt-3"
              @click="otherCompanyProfile(contract.CompanyID)"
              style="width: 95%; text-align: left;">
              <strong> <span style="font-size: x-large;">

              {{ contract.ContractName }}
               | {{ getCompanyName(contract.CompanyID) }} :
              <div v-if="contract.DeveloperID == profile.DeveloperID"
              style="color: green">
                ACCEPTED
              </div>
              <div v-else-if="!contract.Open"
              style="color: red">
                REJECTED
              </div>
              <div v-else-if="contract.Open"
              style="color: orange">
                PENDING
              </div>

              </span> </strong>
              </button>

            </div>
          </div>
        </div>

      </div>
    </div>
    </div>

    </div>
  </div>
</template>

<style sco>
.scroll {
    overflow-y: scroll;
    height: 65vh;
    width: 100%;
    margin: 10px 0 10px 0;
}
.scroll::-webkit-scrollbar {
    width:5px;
}
.scroll::-webkit-scrollbar-track {
    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3);
    border-radius:5px;
}
.scroll::-webkit-scrollbar-thumb {
    border-radius:5px;
    -webkit-box-shadow: inset 0 0 6px black;
}
a {color: inherit; }
</style>
