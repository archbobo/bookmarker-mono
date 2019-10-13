<template>
    <v-container>
        <div v-for="tag in tags" :key="tag.name">
            <v-btn small color="info"
                   :to="{name: 'BookmarksByTag', params: {tag: tag.name}}">
                <i class="fa fa-tags" aria-hidden="true"></i>{{'&nbsp;'+ tag.name}}
            </v-btn>
        </div>
    </v-container>
</template>
<script>
    export default {
        name: 'TagCloud',
        props: {
            tags: { default: [] }
        }
    }
</script>

<style scoped>

</style>
