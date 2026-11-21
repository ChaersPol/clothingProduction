// ECharts 按需引入，减小打包体积
import * as echarts from 'echarts/core'
import { BarChart, PieChart, GaugeChart } from 'echarts/charts'
import { TooltipComponent, GridComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

echarts.use([
  BarChart,
  PieChart,
  GaugeChart,
  TooltipComponent,
  GridComponent,
  CanvasRenderer
])

export default echarts
