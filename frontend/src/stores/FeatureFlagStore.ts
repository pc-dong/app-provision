import { createStore } from 'vuex'
import axios from "axios";

const SERVER_HOST = "http://localhost:8088"
const featureFlagStore = createStore({
    state() {
        return {
            featureFlags: [],
            total: 0,
            page: 0,
            pageSize: 10
        }
    },
    mutations: {
        loadData(state, payload) {
            state.featureFlags = payload.content;
            state.total = payload.total;
            state.page = payload.page + 1
            state.pageSize = payload.pageSize;
        }
    },
    actions: {
        fetchFeatureFlags(context) {
             axios.get(SERVER_HOST + '/feature-flags/page?page=' + (context.state.page > 1 ?  context.state.page - 1 : 0) + '&pageSize=' + context.state.pageSize)
                .then(response => {context.commit("loadData", response.data)})
                .catch(error => console.log(error))
        },
        async submitAddForm(context, form) {
            console.log(JSON.stringify(form))
           return axios.post(SERVER_HOST + '/feature-flags', form)
        },
        async submitEditForm(context, form) {
            return axios.put(SERVER_HOST + '/feature-flags/' + form.featureKey, form.description)
        },
        async publishFeatureFlag(context, featureKey) {
            return axios.put(SERVER_HOST + '/feature-flags/' + featureKey + '/publish')

        },
        async disableFeatureFlag(context, featureKey) {
            return axios.put(SERVER_HOST + '/feature-flags/' + featureKey + '/disable')

        },
        async deleteFeatureFlag(context, featureKey) {
            return axios.delete(SERVER_HOST + '/feature-flags/' + featureKey )
        }
    }
})

export default featureFlagStore