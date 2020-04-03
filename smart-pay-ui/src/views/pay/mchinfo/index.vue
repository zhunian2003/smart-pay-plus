<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="68px">
      <el-form-item label="商户编码" prop="mchId">
        <el-input
          v-model="queryParams.mchId"
          placeholder="请输入商户编码"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商户名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入商户名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="商户状态" clearable size="small">
          <el-option
            v-for="dict in statusOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['pay:mchinfo:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['pay:mchinfo:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['pay:mchinfo:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['pay:mchinfo:export']"
        >导出</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="mchinfoList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="商户编码" align="center" prop="mchId" />
      <el-table-column label="商户名称" align="center" prop="name" />
      <el-table-column label="商户类型" align="center" prop="type" :formatter="typeFormat"/>
      <el-table-column label="状态" align="center" prop="status" :formatter="statusFormat" >
        <template slot-scope="scope">
          <el-popover trigger="hover" placement="top">
            <p>{{ statusFormat(scope.row,scope.column) }}</p>
            <div slot="reference" class="name-wrapper">
              <el-tag size="medium">{{statusFormat(scope.row,scope.column)}}</el-tag>
            </div>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['pay:mchinfo:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['pay:mchinfo:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改岗位对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="商户名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商户名称" />
        </el-form-item>
        <el-form-item label="商户类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择">
            <el-option
              v-for="dict in typesOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="请求私钥" prop="reqKey">
          <el-input v-model="form.reqKey" controls-position="right" placeholder="请输入请求私钥" />
        </el-form-item>
        <el-form-item label="响应私钥" prop="resKey">
          <el-input v-model="form.resKey" controls-position="right" placeholder="请输入响应私钥" />
        </el-form-item>
        <el-form-item label="商户状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in statusOptions"
              :key="dict.dictValue"
              :label="dict.dictValue"
            >{{dict.dictLabel}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import { listMchInfo, getMchInfo, delMchInfo, addMchInfo, updateMchInfo, exportMchInfo } from "@/api/pay/mchinfo";
  import "@riophae/vue-treeselect/dist/vue-treeselect.css";

  export default {
    name: "Mchinfo",
    data() {
      return {
        // 遮罩层
        loading: true,
        // 选中数组
        ids: [],
        // 非单个禁用
        single: true,
        // 非多个禁用
        multiple: true,
        // 总条数
        total: 0,
        // 商户表格数据
        mchinfoList: [],
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 状态数据字典
        statusOptions: [],
        // 状态数据字典
        typesOptions: [],
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          mchId: undefined,
          name: undefined,
          status: undefined
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          name: [
            { required: true, message: "商户名称不能为空", trigger: "blur" }
          ],
          type: [
            { required: true, message: "商户类型不能为空", trigger: "blur" }
          ],
          reqKey: [
            { required: true, message: "请求私钥不能为空", trigger: "blur" }
          ],
          resKey: [
            { required: true, message: "响应私钥不能为空", trigger: "blur" }
          ],
          status: [
            { required: true, message: "商户状态不能为空", trigger: "blur" }
          ]
        }
      };
    },
    created() {
      this.getList();
      this.getDicts("sys_normal_disable").then(response => {
        this.statusOptions = response.data;
      });
      this.getDicts("sys_mch_type").then(response => {
        this.typesOptions = response.data;
      });
    },
    methods: {
      /** 查询商户列表 */
      getList() {
        this.loading = true;
        listMchInfo(this.queryParams).then(response => {
          this.mchinfoList = response.rows;
          this.total = response.total;
          this.loading = false;
        });
      },
      // 商户状态字典翻译
      statusFormat(row, column) {
        return this.selectDictLabel(this.statusOptions, row.status);
      },
      //商户类型
      typeFormat(row, column) {
        return this.selectDictLabel(this.typesOptions, row.type);
      },
      // 取消按钮
      cancel() {
        this.open = false;
        this.reset();
      },
      // 表单重置
      reset() {
        this.form = {
          mchId: undefined,
          name: undefined,
          type: undefined,
          reqKey: undefined,
          resKey: undefined,
          status: "1",
          remark: undefined
        };
        this.resetForm("form");
      },
      /** 搜索按钮操作 */
      handleQuery() {
        this.queryParams.pageNum = 1;
        this.getList();
      },
      /** 重置按钮操作 */
      resetQuery() {
        this.resetForm("queryForm");
        this.handleQuery();
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.mchId)
        this.single = selection.length!=1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加商户";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.reset();
        const mchId = row.mchId || this.ids
        getMchInfo(mchId).then(response => {
          this.form = response.data;
          this.open = true;
          this.title = "修改商户";
        });
      },
      /** 提交按钮 */
      submitForm: function() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            if (this.form.mchId != undefined) {
              updateMchInfo(this.form).then(response => {
                if (response.code === 200) {
                  this.msgSuccess("修改成功");
                  this.open = false;
                  this.getList();
                } else {
                  this.msgError(response.msg);
                }
              });
            } else {
              addMchInfo(this.form).then(response => {
                if (response.code === 200) {
                  this.msgSuccess("新增成功");
                  this.open = false;
                  this.getList();
                } else {
                  this.msgError(response.msg);
                }
              });
            }
          }
        });
      },
      /** 删除按钮操作 */
      handleDelete(row) {
        const mchIds = row.mchId || this.ids;
        this.$confirm('是否确认删除商户编号为"' + mchIds + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return delMchInfo(mchIds);
        }).then(() => {
          this.getList();
          this.msgSuccess("删除成功");
        }).catch(function() {});
      },
      /** 导出按钮操作 */
      handleExport() {
        const queryParams = this.queryParams;
        this.$confirm('是否确认导出所有商户数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return exportMchInfo(queryParams);
        }).then(response => {
          this.download(response.msg);
        }).catch(function() {});
      }
    }
  };
</script>
