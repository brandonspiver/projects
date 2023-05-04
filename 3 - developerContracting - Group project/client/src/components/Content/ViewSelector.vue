<script>
import ButtonBar from '@/components/Bars/ButtonBar.vue';
import GetContracts from './ViewContracts.vue';
import GetDevelopers from './ViewDevelopers.vue';

export default {
  components: { GetContracts, GetDevelopers, ButtonBar },
  computed: {
    isAuthenticated() {
      return this.$store.getters.isAuthenticated;
    },
  },
};
</script>

<template>
  <div>
    <ButtonBar />
    <div v-if="this.$store.state.isDeveloper || !isAuthenticated">
      <GetContracts />
    </div>
    <div v-else>
      <GetDevelopers />
    </div>
  </div>
</template>
