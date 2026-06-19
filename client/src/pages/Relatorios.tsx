import DashboardLayout from "@/components/DashboardLayout";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { BarChart3, TrendingUp, Package, DollarSign, Users, AlertCircle } from "lucide-react";
import { useState } from "react";
import { trpc } from "@/lib/trpc";

export default function Relatorios() {
  const [periodo, setPeriodo] = useState<"dia" | "semana" | "mes" | "ano">("mes");

  const vendasQuery = trpc.vendas.getVendas.useQuery();
  const medicamentosQuery = trpc.catalogo.getMedicamentos.useQuery();
  const comissoesQuery = trpc.vendas.getComissoesPorVendedor.useQuery();

  const vendas = vendasQuery.data || [];
  const medicamentos = medicamentosQuery.data || [];
  const comissoes = comissoesQuery.data || [];

  const totalFaturamento = vendas.reduce((sum, v: any) => sum + v.total, 0);
  const totalVendas = vendas.length;
  const medicamentosVendidos = vendas.reduce((sum, v: any) => sum + (v.itens?.length || 0), 0);
  const ticketMedio = totalVendas > 0 ? totalFaturamento / totalVendas : 0;
  const medicamentosComBaixoEstoque = medicamentos.filter((m: any) => m.estoque <= m.estoqueMinimo);

  return (
    <DashboardLayout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold text-slate-900">Relatórios e Análises</h1>
            <p className="text-slate-600 mt-2">Visualize dados e métricas do seu negócio</p>
          </div>
          <div className="flex gap-2">
            <Button
              variant={periodo === "dia" ? "default" : "outline"}
              onClick={() => setPeriodo("dia")}
              size="sm"
            >
              Dia
            </Button>
            <Button
              variant={periodo === "semana" ? "default" : "outline"}
              onClick={() => setPeriodo("semana")}
              size="sm"
            >
              Semana
            </Button>
            <Button
              variant={periodo === "mes" ? "default" : "outline"}
              onClick={() => setPeriodo("mes")}
              size="sm"
            >
              Mês
            </Button>
            <Button
              variant={periodo === "ano" ? "default" : "outline"}
              onClick={() => setPeriodo("ano")}
              size="sm"
            >
              Ano
            </Button>
          </div>
        </div>

        {/* KPIs */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
          <Card className="border-0 shadow-sm hover:shadow-md transition-shadow">
            <CardHeader className="pb-3">
              <CardTitle className="text-sm font-medium text-slate-600 flex items-center gap-2">
                <DollarSign className="w-4 h-4 text-emerald-600" />
                Faturamento
              </CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-2xl font-bold text-slate-900">R$ {totalFaturamento.toFixed(2)}</p>
              <p className="text-xs text-slate-500 mt-1">Período: {periodo}</p>
            </CardContent>
          </Card>

          <Card className="border-0 shadow-sm hover:shadow-md transition-shadow">
            <CardHeader className="pb-3">
              <CardTitle className="text-sm font-medium text-slate-600 flex items-center gap-2">
                <TrendingUp className="w-4 h-4 text-blue-600" />
                Vendas
              </CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-2xl font-bold text-slate-900">{totalVendas}</p>
              <p className="text-xs text-slate-500 mt-1">Transações</p>
            </CardContent>
          </Card>

          <Card className="border-0 shadow-sm hover:shadow-md transition-shadow">
            <CardHeader className="pb-3">
              <CardTitle className="text-sm font-medium text-slate-600 flex items-center gap-2">
                <Package className="w-4 h-4 text-purple-600" />
                Medicamentos
              </CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-2xl font-bold text-slate-900">{medicamentosVendidos}</p>
              <p className="text-xs text-slate-500 mt-1">Vendidos</p>
            </CardContent>
          </Card>

          <Card className="border-0 shadow-sm hover:shadow-md transition-shadow">
            <CardHeader className="pb-3">
              <CardTitle className="text-sm font-medium text-slate-600 flex items-center gap-2">
                <Users className="w-4 h-4 text-orange-600" />
                Clientes
              </CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-2xl font-bold text-slate-900">{medicamentos.length}</p>
              <p className="text-xs text-slate-500 mt-1">Cadastrados</p>
            </CardContent>
          </Card>

          <Card className="border-0 shadow-sm hover:shadow-md transition-shadow">
            <CardHeader className="pb-3">
              <CardTitle className="text-sm font-medium text-slate-600 flex items-center gap-2">
                <BarChart3 className="w-4 h-4 text-pink-600" />
                Ticket Médio
              </CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-2xl font-bold text-slate-900">R$ {ticketMedio.toFixed(2)}</p>
              <p className="text-xs text-slate-500 mt-1">Por venda</p>
            </CardContent>
          </Card>
        </div>

        {/* Gráficos */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <Card className="border-0 shadow-sm">
            <CardHeader>
              <CardTitle>Vendas por Dia</CardTitle>
              <CardDescription>Evolução de vendas no período</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="h-64 flex items-center justify-center bg-slate-50 rounded-lg">
                <p className="text-slate-500">Gráfico de vendas</p>
              </div>
            </CardContent>
          </Card>

          <Card className="border-0 shadow-sm">
            <CardHeader>
              <CardTitle>Medicamentos Mais Vendidos</CardTitle>
              <CardDescription>Top 10 produtos</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="h-64 flex items-center justify-center bg-slate-50 rounded-lg">
                <p className="text-slate-500">Gráfico de produtos</p>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Tabelas de Dados */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <Card className="border-0 shadow-sm">
            <CardHeader>
              <CardTitle>Estoque Crítico</CardTitle>
              <CardDescription>Medicamentos com estoque baixo</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-3">
                {medicamentosComBaixoEstoque.length === 0 ? (
                  <div className="text-center py-6 text-slate-500">
                    <AlertCircle className="w-8 h-8 mx-auto mb-2 text-emerald-600" />
                    <p className="text-sm">Nenhum medicamento com estoque crítico</p>
                  </div>
                ) : (
                  medicamentosComBaixoEstoque.map((med: any) => (
                    <div key={med.id} className="flex items-center justify-between p-3 bg-orange-50 rounded border border-orange-200">
                      <div>
                        <p className="font-medium text-slate-900">{med.nome}</p>
                        <p className="text-xs text-slate-500">Estoque: {med.estoque} un.</p>
                      </div>
                      <Badge variant="destructive">Crítico</Badge>
                    </div>
                  ))
                )}
              </div>
            </CardContent>
          </Card>

          <Card className="border-0 shadow-sm">
            <CardHeader>
              <CardTitle>Comissões de Vendedores</CardTitle>
              <CardDescription>Desempenho do período</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-3">
                {comissoes.length === 0 ? (
                  <div className="text-center py-6 text-slate-500">
                    <p className="text-sm">Nenhum vendedor com comissões</p>
                  </div>
                ) : (
                  comissoes.map((item: any) => (
                    <div key={item.vendedor?.id} className="flex items-center justify-between p-3 bg-slate-50 rounded border border-slate-200">
                      <div>
                        <p className="font-medium text-slate-900">{item.vendedor?.nome}</p>
                        <p className="text-xs text-slate-500">Comissão: R$ {item.total.toFixed(2)}</p>
                      </div>
                      <Badge variant="outline">{item.vendas} vendas</Badge>
                    </div>
                  ))
                )}
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </DashboardLayout>
  );
}
