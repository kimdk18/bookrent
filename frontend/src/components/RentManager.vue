

<template>
<div>
  <h1 style = "margin-left:4.5%; margin-top:-10px; margin-bottom:20px;">Rent</h1>

    <v-row>
        <Rent class="video-card" v-for="(value, index) in values" v-model="values[index]" v-bind:key="index" @delete="remove"/>
        <Rent class="video-card" :isNew="true" :editMode="true" v-model="newValue" @add="append"/>
    </v-row>

</div>
</template>

<script>

const axios = require('axios').default;
import Rent from './Rent.vue';

export default {
  name: 'RentManager',

  components: {
    Rent
  },

  data: () => ({
    values: [],
    newValue: {}
  }),

  async created() {
      var temp = await axios.get(axios.fixUrl('/rents'))

      this.values = temp.data._embedded.rents;

  },

  methods:{
    append(value){
      this.values.push(value)
      this.newValue = {}
    },

    remove(value){

      var where = -1;
      for(var i=0; i<this.values.length; i++){
        if(this.values[i]._links.self.href == value._links.self.href){
          where = i;
          break;
        }
      }

      if(where > -1){
        this.values.splice(i, 1);
      }
    }    
  }
};
</script>

<style>
  .video-card{width:300px; margin-left:4.5%; margin-top:50px; margin-bottom:50px;}
</style>
