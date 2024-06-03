<template>
    <div class="factory-logo">
      <img :src="factory.image" alt="Factory Logo" />
    </div>
  </template>
  
  <script>
  export default {
    props: {
      factory: {
        type: Object,
        required: true
      }
    }
  };
  </script>
  
  <style scoped>
  .factory-logo img {
    width: 80px;
    height: 80px;
    object-fit: cover;
    border-radius: 50%;
    margin-right: 10px;
  }
  </style>