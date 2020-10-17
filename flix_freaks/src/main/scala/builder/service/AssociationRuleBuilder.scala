package main.scala.builder.service

trait AssociationRuleBuilder[F[_], PlayEvent, GenerateTransactions, AssociationRule] {

  def generateTransactions(data: Seq[PlayEvent]): F[GenerateTransactions]
  def calculateSupportConfidence(data: GenerateTransactions, confidence: Double): F[Seq[AssociationRule]]

}
