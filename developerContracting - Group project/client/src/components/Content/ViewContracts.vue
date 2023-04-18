<script>
import axios from 'axios';

export default {
  data() {
    return {
      contracts: [],
      sortedContracts: [],
      companies: [],
      developers: [],
      apply: [],
      searchText: '',
      disabled: 1,
      filter: '',
    };
  },
  methods: {
    getContract() {
      let path = 'http://localhost:5000/api/contracts';
      axios.get(path)
        .then((res) => {
          this.contracts = res.data.contracts;
          this.sortedContracts = res.data.contracts;
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
    getSearchText(value) {
      this.searchText = value;
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
    applyContract(id) {
      if (!this.$store.getters.isAuthenticated) {
        this.$router.push('/userlogin/');
      } else {
        const path = 'http://localhost:5000/api/apply/';
        axios.post(path, { ContractID: id, DeveloperID: this.$store.state.userData.DeveloperID })
          .then((res) => {
            this.apply.push(res.data);
          })
          .catch((error) => {
            // eslint-disable-next-line
            console.error(error);
          });
      }
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
    resetContract() {
      this.disabled = (this.disabled + 1) % 2;
      this.sortedContracts = this.contracts.slice();
    },
    compare_length_increasing(a, b) {
      if (a.ContractLength < b.ContractLength) {
        return -1;
      }
      if (a.ContractLength > b.ContractLength) {
        return 1;
      }
      return 0;
    },
    compare_length_decreasing(a, b) {
      if (a.ContractLength > b.ContractLength) {
        return -1;
      }
      if (a.ContractLength < b.ContractLength) {
        return 1;
      }
      return 0;
    },
    compare_office(a, b) {
      if (a.InOffice && !b.InOffice) {
        return -1;
      }
      if (!a.InOffice && b.InOffice) {
        return 1;
      }
      return 0;
    },
    compare_remote(a, b) {
      if (!a.InOffice && b.InOffice) {
        return -1;
      }
      if (a.InOffice && !b.InOffice) {
        return 1;
      }
      return 0;
    },
    compare_value_increasing(a, b) {
      if (a.ContractValue < b.ContractValue) {
        return -1;
      }
      if (a.ContractValue > b.ContractValue) {
        return 1;
      }
      return 0;
    },
    compare_value_decreasing(a, b) {
      if (a.ContractValue > b.ContractValue) {
        return -1;
      }
      if (a.ContractValue < b.ContractValue) {
        return 1;
      }
      return 0;
    },
    sort() {
      if (this.disabled === 0) {
        if (this.filter === 'Increasing_Length') {
          this.sortedContracts.sort(this.compare_length_increasing);
        } else if (this.filter === 'Decreasing_Length') {
          this.sortedContracts.sort(this.compare_length_decreasing);
        }
        if (this.filter === 'InOffice') {
          this.sortedContracts.sort(this.compare_office);
        } else if (this.filter === 'Remote') {
          this.sortedContracts.sort(this.compare_remote);
        }
        if (this.filter === 'Increasing_Value') {
          this.sortedContracts.sort(this.compare_value_increasing);
        } else if (this.filter === 'Decreasing_Value') {
          this.sortedContracts.sort(this.compare_value_decreasing);
        }
      }
    },
  },
  created() {
    this.getContract();
  },
  beforeMount() {
    this.getContract();
  },
};
</script>

<template>
  <div class="container-fluid">
    <div class="row">
      <div class="col-2">
        <div class="card bg-light-aqua p-3 mt-3 border-0 mx-2">
          <button class="shadow btn btn-light" style="pointer-events: none;">
          <strong><span style="font-size: x-large; color: var(--aqua)">
              Filters:
          </span></strong>
          </button>

          <button class="shadow btn btn-light mt-2"
          style="pointer-events: none;">
            <span style="font-size: x-large; color: var(--aqua)">
              Length
            </span>
          </button>
          <div class="card p-3 border-0 mt-2"
          style="background-color: var(--aqua); color: white;">

            <div class="form-check form-check-inline">
              <label class="form-check-label" name="radios2" for="inlineCheckbox1">
                <input class="form-check-input" :disabled="disabled == 1"
                type="radio" name="radios2" id="inlineCheckbox1" value="Increasing_Length"
                v-model="filter">
                Increasing
              </label>
            </div>
            <div class="form-check form-check-inline">
              <label class="form-check-label" name="radios2" for="inlineCheckbox2">
                <input class="form-check-input" :disabled="disabled == 1"
                type="radio" name="radios2" id="inlineCheckbox2" value="Decreasing_Length"
                v-model="filter">
                Decreasing
                </label>
            </div>

          </div>

          <button class="shadow btn btn-light mt-2"
          style="pointer-events: none;">
            <span style="font-size: x-large; color: var(--aqua)">
              Environment
            </span>
          </button>
          <div class="card p-3 border-0 mt-2"
          style="background-color: var(--aqua); color: white;">

            <div class="form-check form-check-inline">
              <label class="form-check-label" name="radios2"
              for="inlineCheckbox3">
                <input class="form-check-input" :disabled="disabled == 1"
                type="radio" name="radios2" id="inlineCheckbox3" value="InOffice"
                v-model="filter">
                In office
              </label>
            </div>
            <div class="form-check form-check-inline">
              <label class="form-check-label" name="radios2"
              for="inlineCheckbox4">
                <input class="form-check-input" :disabled="disabled == 1"
                type="radio" name="radios2" id="inlineCheckbox4" value="Remote"
                v-model="filter">
                Remote
                </label>
            </div>

          </div>

          <button class="shadow btn btn-light mt-2"
          style="pointer-events: none;">
            <span style="font-size: x-large; color: var(--aqua)">
              Value
            </span>
          </button>
          <div class="card p-3 border-0 mt-2"
          style="background-color: var(--aqua); color: white;">

            <div class="form-check form-check-inline">
              <label class="form-check-label" name="radios2"
              for="inlineCheckbox5">
                <input class="form-check-input" :disabled="disabled == 1"
                type="radio" name="radios2" id="inlineCheckbox5" value="Increasing_Value"
                v-model="filter">
                Increasing
              </label>
            </div>
            <div class="form-check form-check-inline">
              <label class="form-check-label" name="radios2"
              for="inlineCheckbox6">
                <input class="form-check-input" :disabled="disabled == 1"
                type="radio" name="radios2" id="inlineCheckbox6" value="Decreasing_Value"
                v-model="filter">
                Decreasing
                </label>
            </div>

          </div>

          <div class="hstack gap-1 mt-2">
            <button v-if="this.disabled == 0"
            class="w-50 btn btn-cyan btn-sm" @click="resetContract">
            Toggle Disable
            </button>
            <button v-if="this.disabled == 1"
            class="w-50 btn btn-cyan btn-sm" @click="resetContract">
              Toggle Enable
            </button>
            <button class="w-50 btn btn-cyan btn-sm" @click="sort">
              Apply
            </button>
          </div>

        </div>
      </div>
      <div class="col-10">
        <div class="card bg-light-aqua p-3 mt-4 border-0 mx-2">
          <div class="hstack gap-3">
            <input v-model="searchText"
            class="form-control" type="text"
            placeholder="Search..."
            aria-label="Search..."/>
          </div>
          <div class="scroll">
            <div v-for="(contract, index) in sortedContracts" :key="index">
            <div v-if="(contract.ContractDescription.search(new RegExp(searchText, 'i')) != -1
            || contract.ContractName.search(new RegExp(searchText, 'i')) != -1
            || contract.OfficeAddress.search(new RegExp(searchText, 'i')) != -1
            || getCompanyName(contract.CompanyID).search(new RegExp(searchText, 'i')) != -1)
            && contract.Open">
            <div class="card bg-white text-black p-3 mt-2 border-0 mx-2">
              <div class="card-body">
                <div class="hstack gap-3">
                  <h3 class="card-title">
                    {{ contract.ContractName }}
                     | <a href="#" @click="otherCompanyProfile(contract.CompanyID)"
                    class="text-decoration-none">{{ getCompanyName(contract.CompanyID) }}
                    </a>
                  </h3>
                  <h4 class="ms-auto" style="color: var(--aqua);">
                    {{ contract.OfficeAddress }}
                  </h4>
                </div>
                <p class="card-text">
                  <small class="text-muted">
                  {{ contract.ContractDescription }}
                  <br>
                  <br>
                  <div v-if="contract.InOffice == 1">
                    Working Environment: Will be working in an office
                  </div>
                  <div v-else>
                    Working Environment: Will be working remotely at home
                  </div>
                  </small>
                </p>
                <div class="hstack gap-3">
                  <p class="card-text">
                    <small class="text-muted">{{ contract.DateIssued.split(" ")[0] }}
                       {{ contract.DateIssued.split(" ")[1] }}
                       {{ contract.DateIssued.split(" ")[2] }}
                       {{ contract.DateIssued.split(" ")[3] }}</small></p>
                  <button class="ms-auto btn btn-success border-0 px-5"
                  @click="applyContract(contract.ContractID)">
                    Apply
                  </button>
                  <h4 class="border p-2 rounded" style="color: var(--aqua);">
                    Term: {{ contract.ContractLength }}y | Salary: R{{ contract.ContractValue }} pm
                  </h4>
                </div>
              </div>
            </div>
            </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
:root {
  --light-aqua: #94C1C6;
  --aqua: #5787A2;
}
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
router-link {
    text-decoration: none;
}
.bg-light-aqua {
  background-color: var(--light-aqua);
}
.bg-aqua {
  background-color: var(--aqua);
}
.btn-cyan {
  background-color: #3FB5BC;
  color: white;
}
a {color: inherit; }
</style>
