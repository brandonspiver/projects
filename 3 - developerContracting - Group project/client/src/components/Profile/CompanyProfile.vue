<script>
import ButtonBar from '@/components/Bars/ButtonBar.vue';

export default {
  components: { ButtonBar },
  data() {
    return {
      profile: this.$store.state.otherProfile,
    };
  },
  methods: {
    Hi() {
      return 'Hello World!';
    },
  },
};
</script>

<template>
  <div class="container-fluid">
    <ButtonBar />
    <div class="card bg-white text-black p-3 mt-3 border-0 mx-2">
      <div class="card-body">
        <h1 class="card-title">
          Company Info
        </h1>
        <br>
        <div class="card-text">

          <div class="row">
            <div class="col-5">
              <h4 style="color: gray;">NAME:</h4>
            </div>
            <div class="col">
              <h4>{{ profile.CompanyName }}</h4>
            </div>
          </div>

          <hr style="opacity: 20%;">

          <div class="row">
            <div class="col-5">
              <h4 style="color: gray;">GENERAL INDUSTRY:</h4>
            </div>
            <div class="col">
              <h4>{{ profile.GeneralIndustry }}</h4>
            </div>
          </div>

          <hr style="opacity: 20%;">

          <div class="row">
            <div class="col-5">
              <h4 style="color: gray;">TIME IN BUSINESS:</h4>
            </div>
            <div class="col">
              <h4>{{ profile.YearEstablished }} years</h4>
            </div>
          </div>

          <hr style="opacity: 20%;">

          <div class="row">
            <div class="col-5">
              <h4 style="color: gray;">HQ ADDRESS:</h4>
            </div>
            <div class="col">
              <h4>{{ profile.CompanyAddress }} </h4>
            </div>
          </div>

          <hr style="opacity: 20%;">

          <div class="row">
            <div class="col-5">
              <h4 style="color: gray;">ADMIN EMAIL:</h4>
            </div>
            <div class="col">
              <h4>{{ profile.CompanyEmail }} </h4>
            </div>
          </div>

          <hr style="opacity: 20%;">

          <div class="row">
            <div class="col-5">
              <h4 style="color: gray;">PHONE:</h4>
            </div>
            <div class="col">
              <h4>{{ profile.CompanyPhone }} </h4>
            </div>
          </div>

        </div>
      </div>
    </div>

  </div>
</template>

<style sco>

</style>
