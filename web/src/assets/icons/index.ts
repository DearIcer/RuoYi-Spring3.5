// @ts-ignore
const allModules = import.meta.glob('./svg/*.svg')

const modules: string[] = []
for (const path in allModules) {
  const p = path.split('svg/')[1].split('.svg')[0]
  modules.push(p)
}

export default modules