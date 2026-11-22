<template>
  <div class="app-container garment-page">
    <el-tabs v-model="activeTab" class="basics-tabs">
      <el-tab-pane label="颜色管理" name="color">
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" v-hasPermi="['garment:basics:add']" @click="openColor()">新增颜色</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" v-hasPermi="['garment:basics:remove']" :disabled="multipleColor" @click="delColorRow()">删除</el-button>
          </el-col>
        </el-row>
        <div class="table-wrap">
          <el-table v-loading="colorLoading" :data="colorList" border @selection-change="handleColorSelectionChange">
            <el-table-column v-if="checkPermi(['garment:basics:remove'])" type="selection" width="50" align="center" />
            <el-table-column label="中文名" prop="cnName" min-width="120" />
            <el-table-column label="英文名" prop="enName" min-width="120" />
            <el-table-column label="色卡" width="120" align="center">
              <template #default="s">
                <span class="color-chip" :style="{ background: s.row.colorCode }"></span>
                <span class="muted">{{ s.row.colorCode }}</span>
              </template>
            </el-table-column>
            <el-table-column label="排序" prop="sort" width="80" align="center" />
            <el-table-column v-if="checkPermi(['garment:basics:edit','garment:basics:remove'])" label="操作" class-name="op-cell" width="140" align="center">
              <template #default="s">
                <el-button link type="primary" v-hasPermi="['garment:basics:edit']" @click="openColor(s.row)">修改</el-button>
                <el-button link type="danger" v-hasPermi="['garment:basics:remove']" @click="delColorRow(s.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="尺码管理" name="size">
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" v-hasPermi="['garment:basics:add']" @click="openSize()">新增尺码</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" v-hasPermi="['garment:basics:remove']" :disabled="multipleSize" @click="delSizeRow()">删除</el-button>
          </el-col>
        </el-row>
        <div class="table-wrap">
          <el-table v-loading="sizeLoading" :data="sizeList" border @selection-change="handleSizeSelectionChange">
            <el-table-column v-if="checkPermi(['garment:basics:remove'])" type="selection" width="50" align="center" />
            <el-table-column label="尺码" prop="sizeName" min-width="120" />
            <el-table-column label="排序" prop="sort" width="100" align="center" />
            <el-table-column v-if="checkPermi(['garment:basics:edit','garment:basics:remove'])" label="操作" class-name="op-cell" width="140" align="center">
              <template #default="s">
                <el-button link type="primary" v-hasPermi="['garment:basics:edit']" @click="openSize(s.row)">修改</el-button>
                <el-button link type="danger" v-hasPermi="['garment:basics:remove']" @click="delSizeRow(s.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog :title="colorForm.colorId ? '修改颜色' : '新增颜色'" v-model="colorOpen" width="480px" append-to-body>
      <el-form ref="colorRef" :model="colorForm" :rules="colorRules" label-width="80px">
        <el-form-item label="中文名" prop="cnName"><el-input v-model="colorForm.cnName" /></el-form-item>
        <el-form-item label="英文名"><el-input v-model="colorForm.enName" /></el-form-item>
        <el-form-item label="色值">
          <el-color-picker v-model="colorForm.colorCode" />
          <span class="muted" style="margin-left: 8px">{{ colorForm.colorCode }}</span>
        </el-form-item>
        <el-form-item label="排序"><el-input-number v-model="colorForm.sort" :min="0" controls-position="right" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" v-hasPermi="['garment:basics:add','garment:basics:edit']" @click="submitColor">确 定</el-button>
        <el-button @click="colorOpen = false">取 消</el-button>
      </template>
    </el-dialog>

    <el-dialog :title="sizeForm.sizeId ? '修改尺码' : '新增尺码'" v-model="sizeOpen" width="420px" append-to-body>
      <el-form ref="sizeRef" :model="sizeForm" :rules="sizeRules" label-width="70px">
        <el-form-item label="尺码" prop="sizeName"><el-input v-model="sizeForm.sizeName" placeholder="如 M" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="sizeForm.sort" :min="0" controls-position="right" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" v-hasPermi="['garment:basics:add','garment:basics:edit']" @click="submitSize">确 定</el-button>
        <el-button @click="sizeOpen = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Basics">
import { listColor, addColor, updateColor, delColor, listSize, addSize, updateSize, delSize } from '@/api/garment'

const { proxy } = getCurrentInstance()
const activeTab = ref('color')
const colorIds = ref([])
const multipleColor = ref(true)
const sizeIds = ref([])
const multipleSize = ref(true)

function handleColorSelectionChange(selection) {
  colorIds.value = selection.map(item => item.colorId)
  multipleColor.value = !selection.length
}
function handleSizeSelectionChange(selection) {
  sizeIds.value = selection.map(item => item.sizeId)
  multipleSize.value = !selection.length
}
const colorList = ref([])
const sizeList = ref([])
const colorLoading = ref(false)
const sizeLoading = ref(false)
const colorOpen = ref(false)
const sizeOpen = ref(false)
const colorForm = ref({})
const sizeForm = ref({ colorCode: '#000000' })
const colorRules = { cnName: [{ required: true, message: '颜色名称不能为空', trigger: 'blur' }] }
const sizeRules = { sizeName: [{ required: true, message: '尺码不能为空', trigger: 'blur' }] }

function getColors() {
  colorLoading.value = true
  listColor({ pageNum: 1, pageSize: 200 }).then(res => {
    colorList.value = res.rows
    colorLoading.value = false
  })
}
function getSizes() {
  sizeLoading.value = true
  listSize({ pageNum: 1, pageSize: 100 }).then(res => {
    sizeList.value = res.rows
    sizeLoading.value = false
  })
}
function openColor(row) {
  colorForm.value = row ? { ...row } : { cnName: '', enName: '', colorCode: '#000000', sort: 0, status: '0' }
  colorOpen.value = true
}
function submitColor() {
  proxy.$refs['colorRef'].validate(valid => {
    if (!valid) return
    const fn = colorForm.value.colorId ? updateColor : addColor
    fn(colorForm.value).then(() => {
      proxy.$modal.msgSuccess('保存成功')
      colorOpen.value = false
      getColors()
    })
  })
}
function delColorRow(row) {
  const _ids = row ? [row.colorId] : colorIds.value
  if (!_ids.length) return
  proxy.$modal.confirm(row ? `确认删除颜色「${row.cnName}」？` : `是否确认删除选中的 ${_ids.length} 个颜色？`).then(() => delColor(_ids)).then(() => {
    getColors()
    proxy.$modal.msgSuccess('删除成功')
    colorIds.value = []
    multipleColor.value = true
  }).catch(() => {})
}
function openSize(row) {
  sizeForm.value = row ? { ...row } : { sizeName: '', sort: 0, status: '0' }
  sizeOpen.value = true
}
function submitSize() {
  proxy.$refs['sizeRef'].validate(valid => {
    if (!valid) return
    const fn = sizeForm.value.sizeId ? updateSize : addSize
    fn(sizeForm.value).then(() => {
      proxy.$modal.msgSuccess('保存成功')
      sizeOpen.value = false
      getSizes()
    })
  })
}
function delSizeRow(row) {
  const _ids = row ? [row.sizeId] : sizeIds.value
  if (!_ids.length) return
  proxy.$modal.confirm(row ? `确认删除尺码「${row.sizeName}」？` : `是否确认删除选中的 ${_ids.length} 个尺码？`).then(() => delSize(_ids)).then(() => {
    getSizes()
    proxy.$modal.msgSuccess('删除成功')
    sizeIds.value = []
    multipleSize.value = true
  }).catch(() => {})
}

getColors()
getSizes()

// 从标签页切回本页时自动刷新数据，避免展示缓存中的旧列表
let _pageActivated = false
onActivated(() => {
  if (_pageActivated) {
    getColors()
    getSizes()
  } else {
    _pageActivated = true
  }
})

</script>

<style scoped>
.color-chip {
  display: inline-block;
  width: 16px;
  height: 16px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  vertical-align: middle;
  margin-right: 4px;
}
</style>
