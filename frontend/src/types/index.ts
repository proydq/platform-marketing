// 统一导出所有类型定义

export * from './api'
export * from './customer'
export * from './common'

// 组件相关类型
export * from '../components/base/BaseButton/types'
export * from '../components/base/BaseCard/types'
export * from '../components/base/BaseInput/types'

export * from '../components/common/OmSearchBar/types'
export * from '../components/common/OmTable/types'
export * from '../components/common/OmPagination/types'

// 全局类型声明
declare global {
  interface Window {
    // 全局配置
    __APP_CONFIG__?: {
      env: string
      version: string
      buildTime: string
    }
    
    // 第三方库
    echarts?: any
    AMap?: any
  }
}

// Vue 相关类型扩展
declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    $t: (key: string, values?: any) => string
  }
}

// 环境变量类型
declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

// CSS 模块类型
declare module '*.module.css' {
  const classes: { readonly [key: string]: string }
  export default classes
}

declare module '*.module.scss' {
  const classes: { readonly [key: string]: string }
  export default classes
}

// 图片资源类型
declare module '*.png' {
  const src: string
  export default src
}

declare module '*.jpg' {
  const src: string
  export default src
}

declare module '*.jpeg' {
  const src: string
  export default src
}

declare module '*.gif' {
  const src: string
  export default src
}

declare module '*.svg' {
  const src: string
  export default src
}