<template>
    <div>
        <v-card v-for="bookmark in bookmarks" :key="bookmark.id">
            <v-card-text>
                <h3 class="title">
                    <a style="text-decoration: none" v-bind:href="bookmark.url" target="_blank">{{bookmark.title}}</a>
                </h3>
                <div class="text-sm-left caption">
                    Posted on <strong>{{bookmark.created_at | dt_format}}</strong> By
                    <router-link
                            :to="{name: 'UserProfile', params: {id: bookmark.created_user_id}}">
                        {{bookmark.created_user_name }}
                    </router-link>
                </div>
                <div>
                    <v-btn v-for="tag in bookmark.tags" :key="tag"
                           small color="info"
                           :to="{name: 'BookmarksByTag', params: {tag: tag}}">
                        <i class="fa fa-tags" aria-hidden="true"></i>{{'&nbsp;'+ tag}}
                    </v-btn>
                </div>
            </v-card-text>
        </v-card>
    </div>
</template>
<script>
    import * as moment from 'moment'
    export default {
        name: 'BookmarksList',
        props: {
            bookmarks: { }
        },
        filters: {
            dt_format: function (date) {
                return moment(date).format('LL')
            }
        }
    }
</script>

<style scoped>

</style>
