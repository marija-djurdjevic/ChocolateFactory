<template>
    <div class="modal-overlay" v-if="isVisible">
      <div class="modal-container">
        <button class="close-button" @click="$emit('close')">X</button>
        <slot></slot>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    props: {
      isVisible: {
        type: Boolean,
        default: false
      }
    }
  };
  </script>
  
<style scoped>
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
}

.modal-container {
    background-color: #ffe4b5; 
    border-radius: 10px;
    padding: 20px;
    max-width: 400px;
    width: 100%;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    text-align: center;
    position: relative;
}

.close-button {
    position: absolute;
    top: 10px;
    right: 10px;
    cursor: pointer;
    background: none;
    border: none;
    font-size: 1.5rem;
    color: #333;
}
</style>