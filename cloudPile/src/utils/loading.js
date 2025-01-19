import {computed, ref} from "vue";

// 加载计时器
const _loadingCount = ref(0);

export const loading = computed({
    get(){
        return _loadingCount.value > 0;
    },
    set(val){
        _loadingCount.value = val ? 1 : -1;
        _loadingCount.value = Math.max(0,_loadingCount.value);
    }
})