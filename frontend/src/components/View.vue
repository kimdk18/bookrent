<template>

<v-data-table
    :headers="headers"
    :items="view"
    :items-per-page="5"
    class="elevation-1"
  ></v-data-table>

</template>

<script>
  const axios = require('axios').default;

  export default {
    name: 'View',
    props: {
      value: Object,
      editMode: Boolean,
      isNew: Boolean
    },
    data: () => ({
        headers: [
            { text: "id", value: "id" },
            { text: "rentId", value: "rentId" },
            { text: "bookId", value: "bookId" },
            { text: "deliveryId", value: "deliveryId" },
            { text: "rentStatus", value: "rentStatus" },
            { text: "deliveryStatus", value: "deliveryStatus" },
            { text: "userId", value: "userId" },
        ],
        view : [],
    }),
    async created() {
      var temp = await axios.get(axios.backend + '/views')

      this.view = temp.data._embedded.views;

    },
    methods: {
    }
  }
</script>

