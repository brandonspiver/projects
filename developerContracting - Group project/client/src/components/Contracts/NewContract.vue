<script>
import axios from 'axios';

export default {
  data() {
    return {
      contractss: [],
      companies: [],
      applications: [],
      selectedContract: [],
      selectedDevelopers: [],
      clickedDeveloperID: 0,
      conname: '',
      location: '',
      salary: null,
      description: '',
      length: null,
      stay: null,
      contract: [],
    };
  },
  methods: {
    getContract() {
      let path = 'http://localhost:5000/api/contracts';
      axios.get(path)
        .then((res) => {
          this.contractss = res.data.contracts;
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
    },
    postContract() {
      const path = 'http://localhost:5000/api/new-contract/';
      // eslint-disable-next-line
      axios.post(path, { ContractLength: this.length, ContractValue: this.salary, ContractDescription: this.description, InOffice: this.stay, Open: 1, OfficeAddress: this.location, ContractName: this.conname}, { headers: { Authorization: `Bearer: ${this.$store.state.jwt}` } })
        .then((res) => {
          this.contract = res.data;
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        });
      this.$router.go(0);
    },
    getApplications(id, contract) {
      let path = 'http://localhost:5000/api/applications/';
      this.applications = [];
      this.selectedDevelopers = [];
      this.selectedContract = contract;
      // eslint-disable-next-line
      axios.get(path)
        .then((res) => {
          let i;
          for (i = 0; i < res.data.applications.length; i += 1) {
            if (res.data.applications[i].ContractID === id) {
              this.applications.push(res.data.applications[i]);
            }
          }

          path = 'http://localhost:5000/api/get-developer/';

          for (i = 0; i < this.applications.length; i += 1) {
            axios.post(path, { DeveloperID: this.applications[i].DeveloperID })
              .then((res2) => {
                this.selectedDevelopers.push(res2.data);
              })
              .catch((error) => {
              // eslint-disable-next-line
              console.error(error);
              });
          }
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        });
    },
    otherDeveloperProfile(id) {
      const path = 'http://localhost:5000/api/get-developer/';
      // eslint-disable-next-line
      axios.post(path, { DeveloperID: id })
        .then((res) => {
          this.$store.commit('setOtherProfileData', res.data);
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        });
      this.$router.push('/developerprofile/');
    },
    getSearchText(value) {
      this.searchText = value;
    },
    acceptContract(DeveloperID) {
      const path = 'http://localhost:5000/api/accept-contract/';
      // eslint-disable-next-line
      axios.put(path, { ContractID: this.selectedContract.ContractID, DeveloperID: DeveloperID })
        .then((res) => {
          this.acceptedContract = res.data;
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        });
      this.$router.go(0);
    },
    getCompanyName(id) {
      let i = 0;
      for (i = 0; i < this.companies.length; i += 1) {
        if (this.companies[i].CompanyID === id) {
          return this.companies[i].CompanyName;
        }
      }
      return 0;
    },
  },
  created() {
    this.getContract();
  },
  beforeMount() {
    this.getContract();
    this.companyProfile();
  },
};
</script>

<template>
<div class="container-fluid">
  <div class="row">
    <div class="col-6">
      <div class="rounded" style="background-color: rgba(255, 255, 255, 0.25); width: 100%;
      height: 80vh;">

      <div class="container-fluid pt-3">
        <h4>Contracts:</h4>

        <div class="scroll">
          <div v-for="(contract, index) in contractss" :key="index">

            <div v-if="contract.CompanyID == $store.state.userProfile.CompanyID">
              <div class="container-fluid pt-3">
              <button class="btn btn-light" style="width: 100%; text-align: left;"
              @click="getApplications(contract.ContractID, contract)">
                <strong> <span style="font-size: x-large;">{{ contract.ContractName }}
                  | {{ contract.ContractLength }} years |
                {{ contract.ContractValue}} pm </span> </strong>
              </button>
              </div>
            </div>

          </div>
        </div>

      </div>

      </div>
    </div>
    <div class="col-6">
      <div class="rounded" style="background-color: rgba(255, 255, 255, 0.25); width: 100%;
      height: 80vh;">
        <div class="container-fluid pt-3">
          <form v-on:submit.prevent="postContract" autocomplete="on">
            <div class="form-group">
            <strong>Listing Title: </strong>
            <label for="text"><input type="text" class="form-control"
            id="text" v-model="conname" placeholder="Enter job title" required>
            </label>
            <strong> Location: </strong>
            <label for="text"><input type="text" class="form-control"
            id="text" v-model="location" placeholder="Enter office location" required>
            </label>
            </div>
            <br>

            <div class="form-group">
            <strong>Salary: </strong>
            <label for="text"><input type="text" class="form-control"
            id="text" v-model="salary" placeholder="Enter salary amount" required>
            </label>
            <strong> Contract Description: </strong>
            <label for="text"><input type="text" class="form-control"
            id="text" v-model="description" placeholder="Enter description" required>
            </label>
            </div>
            <br>

            <div class="form-group">
            <strong>Contract Term: </strong>
            <label for="text"><input type="text" class="form-control"
            id="text" v-model="length" placeholder="Enter number of years" required>
            </label>
            <div class="form-check form-check-inline">
              <label class="form-check-label" name="radios2"
              for="inlineCheckbox1">
                <input class="form-check-input"
                type="radio" name="radios2" id="inlineCheckbox1" value=1
                v-model="stay">
                In office
              </label>
            </div>
            <div class="form-check form-check-inline">
              <label class="form-check-label" name="radios2"
              for="inlineCheckbox2">
                <input class="form-check-input"
                type="radio" name="radios2" id="inlineCheckbox2" value=0
                v-model="stay">
                Remote
              </label>
              <button type="submit" name="submit" value="Login"
                class="btn btn-primary">Submit</button>
            </div>
            </div>
            <br>

          </form>

            <div v-if="this.selectedContract.Open == true">
              <h4>Applicants:</h4>
              <div v-for="(dev, index) in selectedDevelopers" :key="index">

                <div class="container-fluid pt-3">
                <button @click="otherDeveloperProfile(dev.DeveloperID)" class="btn btn-light"
                style="width: 80%; text-align: left;">
                  <strong> <span style="font-size: x-large;">
                    {{ dev.Name }} {{ dev.Surname }}
                    | {{ dev.Country }}
                  </span> </strong>
                </button>
                <button @click="acceptContract(dev.DeveloperID)"
                class="btn btn-success mx-2">
                  Accept
                </button>
                </div>

              </div>
            </div>

            <div v-if="this.selectedContract.Open == false">
              <h4>Accepted:</h4>
              <button @click="otherDeveloperProfile(selectedContract.DeveloperID)"
              class="btn btn-light"
              style="width: 80%; text-align: left;">
              View Developer
              </button>
            </div>

        </div>
      </div>
    </div>
  </div>
</div>
</template>

<style scoped>
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
</style>
