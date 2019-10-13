<template>
    <v-container grid-list-md>
        <v-layout row wrap>
            <v-flex xs9>
                <bookmarks-list v-bind:bookmarks="bookmarks.bookmarkList"></bookmarks-list>
            </v-flex>
            <v-flex xs3>
                <tag-cloud v-bind:tags="tags"></tag-cloud>
            </v-flex>

        </v-layout>
    </v-container>
</template>
<script>
    import { mapActions, mapState } from 'vuex'
    import TagCloud from './TagCloud'
    import BookmarksList from './BookmarksList'
    export default {
        name: 'Bookmarks',
        components: {
            'tag-cloud': TagCloud,
            'bookmarks-list': BookmarksList
        },
        data () {
            return {
            }
        },
        mounted () {
            this.fetchTags()
            this.fetchBookmarks()
        },
        computed: {
            ...mapState([ 'bookmarks', 'tags' ])
        },
        methods: {
            ...mapActions([ 'fetchBookmarks', 'fetchTags' ])
        }
    }
</script>

<style scoped>

</style>
