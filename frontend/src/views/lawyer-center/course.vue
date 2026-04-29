<template>
  <div class="course-page">
    <el-card class="mb-20">
      <div slot="header" class="card-header">
        <span>课程管理</span>
        <el-button type="primary" size="small" @click="handleAddCourse">创建课程</el-button>
      </div>
      
      <el-table :data="courseList" border stripe v-loading="loading">
        <el-table-column prop="title" label="课程标题" min-width="200"></el-table-column>
        <el-table-column prop="courseType" label="类型" width="100">
          <template slot-scope="scope">
            {{ scope.row.courseType === 1 ? '公开课' : '系列课' }}
          </template>
        </el-table-column>
        <el-table-column prop="videoCount" label="视频数" width="80"></el-table-column>
        <el-table-column prop="price" label="价格" width="100">
          <template slot-scope="scope">
            {{ scope.row.isFree === 1 ? '免费' : '¥' + scope.row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="学习人数" width="100"></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '已上架' : '未上架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template slot-scope="scope">
            <el-button type="primary" size="small" @click="handleManageVideos(scope.row)">管理视频</el-button>
            <el-button type="text" size="small" @click="handleEditCourse(scope.row)">编辑</el-button>
            <el-button type="text" size="small" v-if="scope.row.status !== 1" @click="handlePublish(scope.row)">上架</el-button>
            <el-button type="text" size="small" class="text-danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        class="mt-20"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        :page-size="pageSize"
        :current-page="currentPage"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      ></el-pagination>
    </el-card>
    
    <el-dialog :title="courseDialogTitle" :visible.sync="courseDialogVisible" width="700px">
      <el-form ref="courseForm" :model="courseForm" :rules="courseRules" label-width="100px">
        <el-form-item label="课程标题" prop="title">
          <el-input v-model="courseForm.title" placeholder="请输入课程标题"></el-input>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程类型">
              <el-select v-model="courseForm.courseType" placeholder="请选择">
                <el-option label="公开课" :value="1"></el-option>
                <el-option label="系列课" :value="2"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否免费">
              <el-radio-group v-model="courseForm.isFree">
                <el-radio :label="1">免费</el-radio>
                <el-radio :label="0">付费</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="价格" v-if="courseForm.isFree === 0">
          <el-input-number v-model="courseForm.price" :min="0" :precision="2"></el-input-number>
        </el-form-item>
        <el-form-item label="课程封面">
          <el-upload
            class="cover-uploader"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            accept="image/*"
          >
            <img v-if="courseForm.coverImage" :src="courseForm.coverImage" class="cover-image">
            <i v-else class="el-icon-plus cover-uploader-icon"></i>
          </el-upload>
        </el-form-item>
        <el-form-item label="课程简介">
          <el-input type="textarea" v-model="courseForm.description" :rows="4" placeholder="请输入课程简介"></el-input>
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="courseForm.tags" placeholder="多个标签用逗号分隔"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="courseDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCourse" :loading="submitting">确定</el-button>
      </div>
    </el-dialog>
    
    <el-dialog title="视频管理" :visible.sync="videoDialogVisible" width="900px" top="5vh">
      <div class="video-header">
        <h4>课程：{{ currentCourse?.title }}</h4>
        <el-button type="primary" size="small" @click="handleAddVideo">添加章节</el-button>
      </div>
      
      <el-table :data="videoList" border stripe v-loading="videoLoading">
        <el-table-column prop="title" label="章节名称" min-width="200"></el-table-column>
        <el-table-column prop="duration" label="时长" width="100">
          <template slot-scope="scope">
            {{ formatDuration(scope.row.duration) }}
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80"></el-table-column>
        <el-table-column prop="viewCount" label="播放量" width="100"></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '已发布' : '未发布' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handlePreview(scope.row)">预览</el-button>
            <el-button type="text" size="small" @click="handleEditVideo(scope.row)">编辑</el-button>
            <el-button type="text" size="small" class="text-danger" @click="handleDeleteVideo(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
    
    <el-dialog :title="videoDialogTitle" :visible.sync="videoFormDialogVisible" width="600px">
      <el-form ref="videoForm" :model="videoForm" :rules="videoRules" label-width="100px">
        <el-form-item label="章节名称" prop="title">
          <el-input v-model="videoForm.title" placeholder="请输入章节名称"></el-input>
        </el-form-item>
        <el-form-item label="上传视频" prop="videoUrl">
          <el-upload
            class="video-uploader"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleVideoSuccess"
            :on-progress="handleVideoProgress"
            accept="video/*"
          >
            <div v-if="videoForm.videoUrl" class="video-preview">
              <video :src="videoForm.videoUrl" controls style="width: 100%; max-height: 200px;"></video>
            </div>
            <div v-else class="video-upload-area">
              <i class="el-icon-video-camera"></i>
              <p>点击上传视频</p>
              <p class="upload-tip">支持 MP4、AVI、MOV 等格式</p>
            </div>
          </el-upload>
          <el-progress v-if="uploadProgress > 0 && uploadProgress < 100" :percentage="uploadProgress" class="mt-10"></el-progress>
        </el-form-item>
        <el-form-item label="视频封面">
          <el-upload
            class="cover-uploader-small"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleVideoCoverSuccess"
            accept="image/*"
          >
            <img v-if="videoForm.coverImage" :src="videoForm.coverImage" class="cover-image-small">
            <i v-else class="el-icon-plus cover-uploader-icon-small"></i>
          </el-upload>
        </el-form-item>
        <el-form-item label="章节序号">
          <el-input-number v-model="videoForm.chapterNo" :min="1" :max="999"></el-input-number>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="videoForm.sortOrder" :min="0" :max="999"></el-input-number>
        </el-form-item>
        <el-form-item label="是否免费试看">
          <el-switch v-model="videoForm.isFree" :active-value="1" :inactive-value="0"></el-switch>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="videoFormDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitVideo" :loading="videoSubmitting">确定</el-button>
      </div>
    </el-dialog>
    
    <el-dialog title="视频预览" :visible.sync="previewDialogVisible" width="800px">
      <div class="preview-container" v-if="previewVideo">
        <video :src="previewVideo.videoUrl" controls style="width: 100%;"></video>
        <h4 class="mt-10">{{ previewVideo.title }}</h4>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getMyCourseList, addCourse, updateCourse, deleteCourse, publishCourse } from '@/api/course'
import { getChapterList, addChapter, updateChapter, deleteChapter } from '@/api/chapter'

export default {
  name: 'LawyerCourse',
  data() {
    return {
      loading: false,
      courseList: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      courseDialogVisible: false,
      courseDialogTitle: '',
      courseForm: {
        id: null,
        title: '',
        courseType: 1,
        isFree: 1,
        price: 0,
        coverImage: '',
        description: '',
        tags: ''
      },
      courseRules: {
        title: [{ required: true, message: '请输入课程标题', trigger: 'blur' }]
      },
      submitting: false,
      videoDialogVisible: false,
      currentCourse: null,
      videoLoading: false,
      videoList: [],
      videoFormDialogVisible: false,
      videoDialogTitle: '',
      videoForm: {
        id: null,
        courseId: null,
        title: '',
        videoUrl: '',
        coverImage: '',
        duration: 0,
        description: '',
        sort: 1,
        isFree: false
      },
      videoRules: {
        title: [{ required: true, message: '请输入章节名称', trigger: 'blur' }],
        videoUrl: [{ required: true, message: '请上传视频', trigger: 'change' }]
      },
      videoSubmitting: false,
      uploadProgress: 0,
      previewDialogVisible: false,
      previewVideo: null,
      uploadUrl: '/api/upload',
      uploadHeaders: {}
    }
  },
  created() {
    const token = this.$store.getters.token
    if (token) {
      this.uploadHeaders = { Authorization: token }
    }
    this.loadData()
  },
  methods: {
    handleSizeChange(val) {
      this.pageSize = val
      this.loadData()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.loadData()
    },
    async loadData() {
      this.loading = true
      try {
        const res = await getMyCourseList({ page: this.currentPage, size: this.pageSize })
        this.courseList = res.data?.records || []
        this.total = res.data?.total || 0
      } catch (error) {
        console.error(error)
      } finally {
        this.loading = false
      }
    },
    handleAddCourse() {
      this.courseDialogTitle = '创建课程'
      this.courseForm = { id: null, title: '', courseType: 1, isFree: 1, price: 0, coverImage: '', description: '', tags: '' }
      this.courseDialogVisible = true
    },
    handleEditCourse(row) {
      this.courseDialogTitle = '编辑课程'
      this.courseForm = { ...row }
      this.courseDialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确定要删除该课程吗？', '提示', { type: 'warning' }).then(async () => {
        try {
          await deleteCourse(row.id)
          this.$message.success('删除成功')
          this.loadData()
        } catch (error) {
          console.error(error)
        }
      }).catch(() => {})
    },
    handlePublish(row) {
      this.$confirm('确定要上架该课程吗？', '提示', { type: 'warning' }).then(async () => {
        try {
          await publishCourse(row.id)
          this.$message.success('上架成功')
          this.loadData()
        } catch (error) {
          console.error(error)
        }
      }).catch(() => {})
    },
    submitCourse() {
      this.$refs.courseForm.validate(async valid => {
        if (valid) {
          this.submitting = true
          try {
            if (this.courseForm.id) {
              await updateCourse(this.courseForm)
            } else {
              await addCourse(this.courseForm)
            }
            this.$message.success('操作成功')
            this.courseDialogVisible = false
            this.loadData()
          } catch (error) {
            console.error(error)
          } finally {
            this.submitting = false
          }
        }
      })
    },
    async handleManageVideos(row) {
      this.currentCourse = row
      this.videoList = []
      this.videoDialogVisible = true
      try {
        const res = await getChapterList(row.id)
        this.videoList = res.data || []
      } catch (error) {
        console.error(error)
      }
    },
    handleAddVideo() {
      this.videoDialogTitle = '添加章节'
      this.videoForm = { id: null, courseId: this.currentCourse.id, title: '', videoUrl: '', coverImage: '', duration: 0, description: '', sort: this.videoList.length + 1, isFree: false }
      this.uploadProgress = 0
      this.videoFormDialogVisible = true
    },
    handleEditVideo(row) {
      this.videoDialogTitle = '编辑章节'
      this.videoForm = { ...row }
      this.uploadProgress = 0
      this.videoFormDialogVisible = true
    },
    handleDeleteVideo(row) {
      this.$confirm('确定要删除该章节吗？', '提示', { type: 'warning' }).then(async () => {
        try {
          await deleteChapter(row.id)
          this.$message.success('删除成功')
          const res = await getChapterList(this.currentCourse.id)
          this.videoList = res.data || []
          this.loadData()
        } catch (error) {
          console.error(error)
        }
      }).catch(() => {})
    },
    handlePreview(row) {
      this.previewVideo = row
      this.previewDialogVisible = true
    },
    handleCoverSuccess(res) {
      if (res.code === 200) {
        this.courseForm.coverImage = res.data.url
      }
    },
    handleVideoSuccess(res) {
      if (res.code === 200) {
        this.videoForm.videoUrl = res.data.url
        this.uploadProgress = 100
      }
    },
    handleVideoProgress(event) {
      this.uploadProgress = Math.floor(event.percent)
    },
    handleVideoCoverSuccess(res) {
      if (res.code === 200) {
        this.videoForm.coverImage = res.data.url
      }
    },
    async submitVideo() {
      this.$refs.videoForm.validate(async valid => {
        if (valid) {
          if (!this.videoForm.videoUrl) {
            this.$message.warning('请上传视频')
            return
          }
          this.videoSubmitting = true
          try {
            const data = {
              courseId: this.currentCourse.id,
              title: this.videoForm.title,
              videoUrl: this.videoForm.videoUrl,
              duration: this.videoForm.duration || 0,
              isFree: this.videoForm.isFree ? 1 : 0,
              sortOrder: this.videoForm.sort || 1
            }
            if (this.videoForm.id) {
              data.id = this.videoForm.id
              await updateChapter(data)
            } else {
              await addChapter(data)
            }
            this.$message.success('操作成功')
            this.videoFormDialogVisible = false
            const res = await getChapterList(this.currentCourse.id)
            this.videoList = res.data || []
            this.loadData()
          } catch (error) {
            console.error(error)
          } finally {
            this.videoSubmitting = false
          }
        }
      })
    },
    formatDuration(seconds) {
      if (!seconds) return '00:00'
      const minutes = Math.floor(seconds / 60)
      const secs = seconds % 60
      return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
    }
  }
}
</script>

<style lang="scss" scoped>
.course-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .video-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h4 {
      margin: 0;
    }
  }
  
  .cover-uploader {
    ::v-deep .el-upload {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      
      &:hover {
        border-color: #1890ff;
      }
    }
    
    .cover-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 200px;
      height: 120px;
      line-height: 120px;
      text-align: center;
    }
    
    .cover-image {
      width: 200px;
      height: 120px;
      display: block;
      object-fit: cover;
    }
  }
  
  .cover-uploader-small {
    ::v-deep .el-upload {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      
      &:hover {
        border-color: #1890ff;
      }
    }
    
    .cover-uploader-icon-small {
      font-size: 28px;
      color: #8c939d;
      width: 120px;
      height: 80px;
      line-height: 80px;
      text-align: center;
    }
    
    .cover-image-small {
      width: 120px;
      height: 80px;
      display: block;
      object-fit: cover;
    }
  }
  
  .video-uploader {
    ::v-deep .el-upload {
      width: 100%;
    }
    
    .video-upload-area {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      padding: 40px;
      text-align: center;
      cursor: pointer;
      
      &:hover {
        border-color: #1890ff;
      }
      
      i {
        font-size: 48px;
        color: #8c939d;
      }
      
      p {
        margin: 10px 0 0;
        color: #666;
      }
      
      .upload-tip {
        font-size: 12px;
        color: #999;
      }
    }
    
    .video-preview {
      border: 1px solid #d9d9d9;
      border-radius: 6px;
      overflow: hidden;
    }
  }
  
  .preview-container {
    h4 {
      margin: 10px 0;
    }
    
    .video-desc {
      color: #666;
      font-size: 14px;
    }
  }
}
</style>
