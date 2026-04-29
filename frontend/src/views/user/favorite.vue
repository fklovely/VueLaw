<template>
  <div class="favorite-page">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="案例收藏" name="case">
        <el-table :data="caseList" border stripe>
          <el-table-column prop="title" label="案例标题" min-width="200"></el-table-column>
          <el-table-column prop="caseType" label="案件类型" width="100"></el-table-column>
          <el-table-column prop="courtName" label="法院" width="150"></el-table-column>
          <el-table-column prop="judgeDate" label="判决日期" width="120"></el-table-column>
          <el-table-column label="操作" width="150">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="$router.push(`/case/detail/${scope.row.id}`)">查看</el-button>
              <el-button type="text" size="small" class="text-danger" @click="handleRemove('case', scope.row.id)">取消收藏</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="法规收藏" name="regulation">
        <el-table :data="regulationList" border stripe>
          <el-table-column prop="title" label="法规标题" min-width="250"></el-table-column>
          <el-table-column prop="regType" label="类型" width="100"></el-table-column>
          <el-table-column prop="issueOrg" label="发布机构" width="150"></el-table-column>
          <el-table-column label="操作" width="150">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="$router.push(`/regulation/detail/${scope.row.id}`)">查看</el-button>
              <el-button type="text" size="small" class="text-danger" @click="handleRemove('regulation', scope.row.id)">取消收藏</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="文章收藏" name="article">
        <el-table :data="articleList" border stripe>
          <el-table-column prop="title" label="文章标题" min-width="200"></el-table-column>
          <el-table-column prop="authorName" label="作者" width="100"></el-table-column>
          <el-table-column prop="publishTime" label="发布时间" width="160"></el-table-column>
          <el-table-column label="操作" width="150">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="$router.push(`/article/detail/${scope.row.id}`)">查看</el-button>
              <el-button type="text" size="small" class="text-danger" @click="handleRemove('article', scope.row.id)">取消收藏</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
export default {
  name: 'UserFavorite',
  data() {
    return {
      activeTab: 'case',
      caseList: [
        { id: 1, title: '未成年人校园欺凌案', caseType: '民事侵权', courtName: '北京市朝阳区人民法院', judgeDate: '2025-12-15' },
        { id: 2, title: '监护权变更纠纷案', caseType: '婚姻家庭', courtName: '上海市静安区人民法院', judgeDate: '2025-11-20' }
      ],
      regulationList: [
        { id: 1, title: '《中华人民共和国未成年人保护法》', regType: '法律', issueOrg: '全国人大常委会' },
        { id: 2, title: '《中华人民共和国家庭教育促进法》', regType: '法律', issueOrg: '全国人大常委会' }
      ],
      articleList: [
        { id: 1, title: '未成年人保护法修订亮点解读', authorName: '张明华律师', publishTime: '2026-02-01' },
        { id: 2, title: '校园欺凌的识别与应对', authorName: '王国强律师', publishTime: '2026-02-05' }
      ]
    }
  },
  methods: {
    handleRemove(type, id) {
      this.$message.success('取消收藏成功')
    }
  }
}
</script>
