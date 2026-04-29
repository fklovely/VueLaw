<template>
  <div class="match-page">
    <div class="match-shell">
      <div class="match-top">
        <section class="match-input-card">
          <div class="section-heading">
            <h2>需求智能匹配</h2>
            <p>描述法律问题，匹配案件类型和合适律师</p>
          </div>

          <el-form label-position="top">
            <el-form-item label="法律问题描述">
              <el-input
                v-model="description"
                type="textarea"
                :rows="8"
                maxlength="500"
                show-word-limit
                placeholder="例如：孩子在学校被同学长期辱骂、推搡，学校一直没有处理，我想知道如何维权。"
              ></el-input>
            </el-form-item>
          </el-form>

          <div class="example-row">
            <span class="example-label">常见场景</span>
            <el-tag
              v-for="item in examples"
              :key="item.label"
              size="medium"
              effect="plain"
              @click="useExample(item.text)"
            >
              {{ item.label }}
            </el-tag>
          </div>

          <div class="action-row">
            <el-button type="primary" icon="el-icon-search" :loading="loading" @click="handleMatch">开始匹配</el-button>
            <el-button icon="el-icon-refresh-left" @click="resetMatch">清空</el-button>
          </div>
        </section>

        <aside class="match-result-card">
          <template v-if="matchResult">
            <div class="result-label">识别案件类型</div>
            <h3>{{ matchResult.caseType }}</h3>
            <el-progress :percentage="confidence" :stroke-width="10"></el-progress>
            <p class="result-desc">{{ matchResult.description }}</p>
            <div class="keyword-list" v-if="matchedKeywords.length">
              <el-tag v-for="word in matchedKeywords" :key="word" size="small" type="success">{{ word }}</el-tag>
            </div>
            <div class="suggestion-box">
              <i class="el-icon-document-checked"></i>
              <span>{{ matchResult.suggestion }}</span>
            </div>
            <el-button type="text" @click="goLawyerList">查看更多该领域律师 <i class="el-icon-arrow-right"></i></el-button>
          </template>
          <el-empty v-else description="暂无匹配结果"></el-empty>
        </aside>
      </div>

      <section class="lawyer-results" v-if="matchedLawyers.length">
        <div class="results-header">
          <div>
            <h3>匹配律师</h3>
            <p>按擅长领域、问题关键词、评分和服务量综合排序</p>
          </div>
          <el-tag type="info">{{ matchedLawyers.length }} 位</el-tag>
        </div>

        <div class="lawyer-grid">
          <div class="lawyer-card" v-for="lawyer in matchedLawyers" :key="lawyer.id">
            <div class="lawyer-main">
              <el-avatar :size="72" :src="lawyer.avatar || defaultAvatar"></el-avatar>
              <div class="lawyer-info">
                <h4>{{ lawyer.lawyerName }}</h4>
                <p class="law-firm">{{ lawyer.lawFirm || '律所信息待完善' }}</p>
                <p class="location"><i class="el-icon-location"></i> {{ lawyer.province || '-' }} {{ lawyer.city || '' }}</p>
              </div>
              <div class="score-box">
                <span>{{ lawyer.matchScore }}</span>
                <small>匹配分</small>
              </div>
            </div>

            <div class="expertise-tags">
              <el-tag v-for="item in getExpertises(lawyer.expertises)" :key="item" size="small">{{ item }}</el-tag>
            </div>

            <p class="reason">{{ lawyer.matchReason }}</p>
            <p class="profile">{{ lawyer.profile || '暂无简介' }}</p>

            <div class="card-footer">
              <div class="meta">
                <span><i class="el-icon-star-on"></i> {{ lawyer.rating || '5.00' }}分</span>
                <span><i class="el-icon-s-custom"></i> {{ lawyer.serviceCount || 0 }}次服务</span>
              </div>
              <div class="buttons">
                <el-button size="small" @click="goDetail(lawyer.id)">详情</el-button>
                <el-button type="primary" size="small" @click="goConsult(lawyer.id)">咨询</el-button>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script>
import { intelligentMatch } from '@/api/lawyer'

export default {
  name: 'DemandMatch',
  data() {
    return {
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      description: '',
      loading: false,
      matchResult: null,
      examples: [
        { label: '未成年人监护权', text: '离婚后孩子一直跟我生活，对方长期不管孩子，我想申请变更监护权和抚养安排。' },
        { label: '校园欺凌', text: '孩子在学校被同学长期辱骂、推搡，还被拍视频发到群里，老师和学校一直没有处理。' },
        { label: '家庭财产继承', text: '父亲去世后留下房产和存款，没有遗嘱，兄弟姐妹对遗产分割和过户有争议。' },
        { label: '家庭暴力', text: '家里有人经常殴打和威胁孩子，已经报警但还担心继续受到伤害，想申请保护。' }
      ]
    }
  },
  computed: {
    matchedLawyers() {
      return this.matchResult && this.matchResult.lawyers ? this.matchResult.lawyers : []
    },
    matchedKeywords() {
      return this.matchResult && this.matchResult.matchedKeywords ? this.matchResult.matchedKeywords : []
    },
    confidence() {
      return this.matchResult && this.matchResult.confidence ? Number(this.matchResult.confidence) : 0
    }
  },
  created() {
    const queryText = this.$route.query.q
    if (queryText) {
      this.description = queryText
      this.handleMatch()
    }
  },
  methods: {
    async handleMatch() {
      const content = this.description.trim()
      if (!content) {
        this.$message.warning('请输入法律问题描述')
        return
      }
      this.loading = true
      try {
        const res = await intelligentMatch({ description: content, limit: 6 })
        this.matchResult = res.data
      } catch (error) {
        console.error(error)
      } finally {
        this.loading = false
      }
    },
    resetMatch() {
      this.description = ''
      this.matchResult = null
    },
    useExample(text) {
      this.description = text
    },
    getExpertises(value) {
      if (!value) return []
      return value.split(',').filter(Boolean).slice(0, 4)
    },
    goDetail(id) {
      this.$router.push(`/lawyer/detail/${id}`)
    },
    goConsult(id) {
      this.$router.push(`/consult/create/${id}`)
    },
    goLawyerList() {
      const expertise = this.matchResult ? this.matchResult.expertise : ''
      this.$router.push({ path: '/lawyer', query: { expertise } })
    }
  }
}
</script>

<style lang="scss" scoped>
.match-page {
  min-height: calc(100vh - 60px);
  background: #f5f7fa;
  padding: 24px 20px 40px;
}

.match-shell {
  max-width: 1200px;
  margin: 0 auto;
}

.match-top {
  display: grid;
  grid-template-columns: minmax(0, 2fr) minmax(320px, 1fr);
  gap: 20px;
  align-items: stretch;
}

.match-input-card,
.match-result-card,
.lawyer-results {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 24px;
}

.section-heading {
  margin-bottom: 18px;

  h2 {
    margin: 0 0 8px;
    font-size: 26px;
    color: #1f2d3d;
  }

  p {
    margin: 0;
    color: #667085;
  }
}

.example-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  margin-top: 4px;

  .example-label {
    color: #606266;
    font-size: 13px;
  }

  .el-tag {
    cursor: pointer;
  }
}

.action-row {
  display: flex;
  gap: 12px;
  margin-top: 22px;
}

.match-result-card {
  display: flex;
  flex-direction: column;

  .result-label {
    color: #909399;
    font-size: 13px;
    margin-bottom: 8px;
  }

  h3 {
    margin: 0 0 16px;
    color: #1f2d3d;
    font-size: 24px;
  }

  .result-desc {
    margin: 16px 0 0;
    color: #606266;
    line-height: 1.7;
  }

  .keyword-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-top: 14px;
  }

  .suggestion-box {
    display: flex;
    gap: 10px;
    margin: 18px 0 8px;
    padding: 14px;
    border-radius: 8px;
    background: #f0f9eb;
    color: #3f7d20;
    line-height: 1.6;

    i {
      margin-top: 3px;
      flex-shrink: 0;
    }
  }

  .el-button {
    align-self: flex-start;
    margin-top: auto;
  }
}

.lawyer-results {
  margin-top: 20px;
}

.results-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  margin-bottom: 18px;

  h3 {
    margin: 0 0 6px;
    color: #1f2d3d;
    font-size: 22px;
  }

  p {
    margin: 0;
    color: #667085;
  }
}

.lawyer-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.lawyer-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 18px;
  transition: all 0.3s;

  &:hover {
    border-color: #1890ff;
    box-shadow: 0 4px 12px rgba(24, 144, 255, 0.14);
  }
}

.lawyer-main {
  display: grid;
  grid-template-columns: 72px minmax(0, 1fr) 72px;
  gap: 14px;
  align-items: center;
}

.lawyer-info {
  min-width: 0;

  h4 {
    margin: 0 0 6px;
    color: #1f2d3d;
    font-size: 18px;
  }

  p {
    margin: 0;
  }

  .law-firm {
    color: #606266;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .location {
    margin-top: 5px;
    color: #909399;
    font-size: 13px;
  }
}

.score-box {
  width: 72px;
  height: 72px;
  border-radius: 8px;
  background: #ecf5ff;
  color: #1890ff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  span {
    font-size: 20px;
    font-weight: 600;
  }

  small {
    margin-top: 3px;
    font-size: 12px;
  }
}

.expertise-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 14px;
}

.reason {
  margin: 14px 0 8px;
  color: #3f7d20;
  line-height: 1.6;
}

.profile {
  color: #606266;
  line-height: 1.6;
  height: 44px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
  margin-top: 16px;
  padding-top: 14px;
  border-top: 1px solid #ebeef5;

  .meta {
    color: #909399;
    font-size: 13px;

    span {
      margin-right: 12px;
    }

    i {
      color: #f7ba2a;
    }
  }

  .buttons {
    display: flex;
    gap: 8px;
    flex-shrink: 0;
  }
}

@media (max-width: 960px) {
  .match-top,
  .lawyer-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .match-page {
    padding: 16px 12px 28px;
  }

  .match-input-card,
  .match-result-card,
  .lawyer-results {
    padding: 18px;
  }

  .lawyer-main {
    grid-template-columns: 56px minmax(0, 1fr);

    .el-avatar {
      width: 56px !important;
      height: 56px !important;
      line-height: 56px !important;
    }
  }

  .score-box {
    grid-column: 1 / -1;
    width: 100%;
    height: 54px;
    flex-direction: row;
    gap: 8px;
  }

  .card-footer {
    align-items: flex-start;
    flex-direction: column;

    .buttons {
      width: 100%;

      .el-button {
        flex: 1;
      }
    }
  }
}
</style>
