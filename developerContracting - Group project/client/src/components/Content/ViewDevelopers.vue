<script>
import axios from 'axios';

export default {
  data() {
    return {
      devs: [],
      companies: [],
      developers: [],
      searchText: '',
    };
  },
  methods: {
    getDevelopers() {
      const path = 'http://localhost:5000/api/developers';
      axios.get(path)
        .then((res) => {
          this.devs = res.data.developers;
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
    sendMissing(missed) {
      const string = `NB Add a ${missed} column to the database!!`;
      return string;
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
  },
  created() {
    this.getDevelopers();
  },
  beforeMount() {
    this.getDevelopers();
  },
};
</script>

<template>
  <!-- <MidBar @searchText="getSearchText"/> -->
  <div class="container-fluid">
    <div class="row">
      <div class="col-12">
        <div class="card bg-light-aqua p-3 mt-4 border-0 mx-2">
          <div class="hstack gap-3">
            <input v-model="searchText"
            class="form-control" type="text"
            placeholder="Search..."
            aria-label="Search..."/>
          </div>
          <div class="scroll">
          <div v-for="(developer, index) in devs" :key="index">
          <div v-if="developer.Name.search(new RegExp(searchText, 'i')) != -1
          || developer.Surname.search(new RegExp(searchText, 'i')) != -1
          || developer.Country.search(new RegExp(searchText, 'i')) != -1">
          <a href="#" @click="otherDeveloperProfile(developer.DeveloperID)"
          class="text-decoration-none">
          <div class="card bg-white text-black p-3 mt-2 border-0
            mx-2">
            <div class="card-body">
              <div class="hstack gap-3">
                <h3 class="card-title">
                  {{ developer.Name }} {{ developer.Surname }}
                </h3>
                <h4 class="ms-auto" style="color: var(--aqua);">
                  {{ developer.Country }}
                </h4>
              </div>
              <p class="card-text">
                <small class="text-muted">
                {{ developer.Description }}
                </small>
              </p>
            </div>
          </div>
          </a>
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
    -webkit-box-shadow: inset 0 0 6px red;
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
</style>
