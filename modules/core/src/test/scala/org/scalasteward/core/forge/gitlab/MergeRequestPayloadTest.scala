package org.scalasteward.core.forge.gitlab

import io.circe.literal._
import io.circe.syntax._
import munit.FunSuite
import org.scalasteward.core.forge.data.NewPullRequestData
import org.scalasteward.core.forge.gitlab.GitLabJsonCodec._
import org.scalasteward.core.git.Branch

class MergeRequestPayloadTest extends FunSuite {
  private val master = Branch("master")
  private val data = NewPullRequestData(
    title = "Test MR title",
    body = "Test MR body",
    head = "source",
    base = master,
    labels = Nil,
    assignees = Nil,
    reviewers = Nil
  )
  private val id = "123"
  private val projectId = 321L

  test("asJson") {
    val obtained = MergeRequestPayload(id, projectId, data, Map.empty).asJson
    val expected =
      json"""{
               "id" : "123",
               "title" : "Test MR title",
               "description" : "Test MR body",
               "assignee_ids" : null,
               "reviewer_ids" : null,
               "target_project_id" : 321,
               "source_branch" : "source",
               "target_branch" : "master"
             }"""
    assertEquals(obtained, expected)
  }

  test("asJson for draft MR") {
    val obtained = MergeRequestPayload(id, projectId, data.copy(draft = true), Map.empty).asJson
    val expected =
      json"""{
               "id" : "123",
               "title" : "Draft: Test MR title",
               "description" : "Test MR body",
               "assignee_ids" : null,
               "reviewer_ids" : null,
               "target_project_id" : 321,
               "source_branch" : "source",
               "target_branch" : "master"
             }"""
    assertEquals(obtained, expected)
  }

  test("asJson with assignees and reviewers") {
    val obtained = MergeRequestPayload(
      id = id,
      projectId = projectId,
      data = data.copy(assignees = List("foo"), reviewers = List("bar")),
      usernamesToUserIdsMapping = Map("foo" -> 1, "bar" -> 2)
    ).asJson
    val expected =
      json"""{
               "id" : "123",
               "title" : "Test MR title",
               "description" : "Test MR body",
               "assignee_ids": [ 1 ],
               "reviewer_ids": [ 2 ],
               "target_project_id" : 321,
               "source_branch" : "source",
               "target_branch" : "master"
             }"""
    assertEquals(obtained, expected)
  }
}
