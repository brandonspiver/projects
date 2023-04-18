<script>
export default {
  methods: {

  },
};
</script>

<template>
  <div class="container-fluid mt-5">
    <div class="row">
      <div class="col-7">
          <div class="card-body">
            <div class="card bg-white text-black p-3 mt-2 border-0">
              <div class="card-body">
                <h3 class="card-title text-center">
                  Are you sure you want to <br>
                  delete this account?
                </h3> <br>

                <button type="yes" name="yes" value="Yes"
                class="btn btn-primary">Yes</button>
                <br>
                <button type="no" name="no" value="No"
                class="btn btn-primary">No</button>
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
</style>

<style scoped>
.btn-light-aqua {
  background-color: var(--light-aqua);
  color: black;
}
</style>
