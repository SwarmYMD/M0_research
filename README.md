# PSOによるパターンフォーメーション問題を扱う研究のプログラム
## 概要
群知能アルゴリズムの一つである粒子群最適化（PSO）を応用し、マルチエージェントパターンフォーメーション問題を解く手法を提案。
その実験を行うプログラム群。
baseline（既存手法）についてはhttps://ieeexplore.ieee.org/abstract/document/5445863/ を参照。

1. PF_PSO_origin.java：baselineの実験検証のためのプログラム。
2. PF_PSO_avoid.java：移動先での重複回避のみを行う、提案手法1のプログラム。
3. PF_PSO_test.java：パターン上に到達したエージェントが、一定時間ごとにランダムに動く、提案手法2のプログラム。
4. PF_PSO_proposal.java
