import { useDictStore } from '@/stores/modules/dict'

/**
 * 获取字典数据
 */
export function useDict(...args: string[]) {
  const res = ref<{ [key: string]: any }>({})
  args.forEach((dictType, index) => {
    res.value[dictType] = []
    const dicts = useDictStore().getDictList(dictType)
    if (dicts) {
      res.value[dictType] = dicts
    }
  })
  return toRefs(res.value)
}