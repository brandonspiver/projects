<script>
import TopBar from '@/components/Bars/TopBar.vue';

export default {
  components: { TopBar },
  data() {
    return {
      image: '@/background.png',
    };
  },
};
</script>

<template>
  <div class="main_background">
    <TopBar />
    <transition name="route" mode="out-in">
      <router-view />
    </transition>
  </div>
</template>

<style>
.main_background {
  background-image: url('../images/background.png');
  background-repeat: no-repeat;
  background-size: cover;
  height: 100vh;
}

.route-enter {
  opacity: 0;
  transform: translateX(100px)
}

.route-enter-active {
  transition: all 0.3s ease-out;
}

.route-leave-to {
  opacity: 0;
  transform: translateX(-100px)
}

.route-leave-active {
  transition: all 0.3s ease-in;
}
</style>
