<script>
export default {
  name: 'ViewContract',
  props: ['contract', 'company_name'],
};
</script>

<template>
  <div class="container-fluid">
    <div class="card bg-light-aqua p-3 mt-2 border-0 mx-5">
      <div class="card bg-white text-black p-3 mt-2 border-0 mx-2">
        <div class="card-body">
          <div class="hstack gap-3">
          <h3 class="card-title">
            Job Title | {{ company_name }}
          </h3>
          <h3 class="ms-auto">
            <div v-if="contract.Open == 1" style="color: green">
              OPEN
            </div>
            <div v-else style="color: red">
              CLOSED
            </div>
          </h3>
          </div>
          <div class="card-text mt-2">
            {{ contract.ContractDescription }}
            <br>
            <br>
            Salary: R{{ contract.ContractValue }} pm
            <br>
            Term: {{ contract.ContractLength }} years
            <div v-if="contract.InOffice == 1">
              Working Environment: Will be working in an office
            </div>
            <div v-else>
              Working Environment: Will be working remotely at home
            </div>
            Preferred Programming Languages:
          </div>
          <br>
          <div class="hstack gap-3">
            <p class="card-text"><small class="text-muted">{{ contract.DateIssued }}</small></p>
            <div class="ms-auto">
              <button class="btn btn-success">
                APPLY NOW
              </button>
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
</style>
