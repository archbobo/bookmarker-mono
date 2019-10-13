<template>
    <v-container grid-list-md>
        <v-layout row wrap>
            <v-flex xs9>
                <v-alert
                        :value="true"
                        color="info"
                        icon="info"
                        outline
                >
                    Bookmarks tagged: <code>{{selectedTag.name}}</code>
                </v-alert>
                <bookmarks-list v-bind:bookmarks="selectedTag.bookmarkList"></bookmarks-list>
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
        name: 'BookmarksByTag',
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
            this.fetchBookmarksByTag(this.$route.params.tag)
        },
        watch: {
            '$route.params.tag': function () {
                this.fetchBookmarksByTag(this.$route.params.tag)
            }
        },
        computed: {
            ...mapState([ 'selectedTag', 'tags' ])
        },
        methods: {
            ...mapActions([ 'fetchBookmarksByTag', 'fetchTags' ])
        }
    }
</script>

<style scoped>

</style>
