<template>
    <NavigationBar/>
    <TicketBoard/>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import TicketBoard from "@/components/TicketBoard.vue";
import NavigationBar from "@/components/NavigationBar.vue";

export default defineComponent({
    name: "Home",
    components: {
        NavigationBar,
        TicketBoard
    },
});
</script>