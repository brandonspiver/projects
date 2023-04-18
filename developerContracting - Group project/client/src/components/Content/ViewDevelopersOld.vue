<script>
import axios from 'axios';

export default {
  data() {
    return {
      developers: [],
      searchText: '',
    };
  },
  methods: {
    getContract() {
      const path = 'http://localhost:5000/developers';
      axios.get(path)
        .then((res) => {
          this.developers = res.data.developers;
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        });
    },
    getSearchText(value) {
      this.searchText = value;
    },
    sendMissing(missed) {
      const string = `NB Add a ${missed} column to the database!!`;
      return string;
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
  <div>
  <div class="container-fluid">
    <div class="card bg-light-aqua p-3 mt-5 border-0
    mx-5">
      <div class="scroll">
        <div v-for="(developer, index) in developers" :key="index">
        <div v-if="developer.Name.search(new RegExp(searchText, 'i')) != -1
        || developer.Description.search(new RegExp(searchText, 'i')) != -1">
        <div class="card bg-white text-black p-3 mt-2 border-0
        mx-2">
          <div class="card-body">
            <img alt="profile photo" :src="developer.Photo" style="float: left; padding: 10px;">
            <div class="hstack gap-3">
              <h3 class="card-title">
                {{ developer.Name }}
              </h3>
              <h4 class="ms-auto" style="color: var(--aqua);">
                REDACTED
              </h4>
            </div>
            <p class="card-text">
              <small class="text-muted">
              {{ developer.Description }}
              </small>
            </p>
            <div class="hstack gap-3">
              <p class="card-text"><small class="text-muted">REDACTED</small></p>
              <h4 class="ms-auto border p-2 rounded" style="color: var(--aqua);">
                REDACTED
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
    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3);
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
</style>
