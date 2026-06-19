import { useAuth } from "@/_core/hooks/useAuth";
import DashboardLayout from "@/components/DashboardLayout";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { getLoginUrl } from "@/const";
import { Pill, ShoppingCart, Users, BarChart3, Package } from "lucide-react";

export default function Home() {
  const { user, isAuthenticated, logout } = useAuth();

  if (!isAuthenticated) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100 flex items-center justify-center p-4">
        <div className="max-w-md w-full">
          <div className="text-center mb-8">
            <div className="flex items-center justify-center gap-3 mb-4">
              <div className="p-3 bg-gradient-to-br from-emerald-500 to-teal-600 rounded-lg">
                <Pill className="w-8 h-8 text-white" />
              </div>
              <h1 className="text-3xl font-bold text-slate-900">Farmácia Pro</h1>
            </div>
            <p className="text-slate-600 text-lg">Sistema Elegante de Gestão Farmacêutica</p>
          </div>

          <Card className="border-0 shadow-xl">
            <CardHeader className="text-center pb-4">
              <CardTitle className="text-2xl">Bem-vindo</CardTitle>
              <CardDescription>Faça login para acessar o dashboard</CardDescription>
            </CardHeader>
            <CardContent>
              <a href={getLoginUrl()}>
                <Button className="w-full bg-gradient-to-r from-emerald-500 to-teal-600 hover:from-emerald-600 hover:to-teal-700 text-white font-semibold py-6 text-lg">
                  Entrar com Manus
                </Button>
              </a>
            </CardContent>
          </Card>

          <div className="mt-8 grid grid-cols-2 gap-4">
            <div className="bg-white rounded-lg p-4 shadow-sm border border-slate-200">
              <Package className="w-6 h-6 text-emerald-600 mb-2" />
              <p className="text-sm font-medium text-slate-900">Catálogo</p>
              <p className="text-xs text-slate-500">Gestão de medicamentos</p>
            </div>
            <div className="bg-white rounded-lg p-4 shadow-sm border border-slate-200">
              <ShoppingCart className="w-6 h-6 text-blue-600 mb-2" />
              <p className="text-sm font-medium text-slate-900">Vendas</p>
              <p className="text-xs text-slate-500">Balcão e online</p>
            </div>
            <div className="bg-white rounded-lg p-4 shadow-sm border border-slate-200">
              <Users className="w-6 h-6 text-purple-600 mb-2" />
              <p className="text-sm font-medium text-slate-900">Clientes</p>
              <p className="text-xs text-slate-500">Gestão de clientes</p>
            </div>
            <div className="bg-white rounded-lg p-4 shadow-sm border border-slate-200">
              <BarChart3 className="w-6 h-6 text-orange-600 mb-2" />
              <p className="text-sm font-medium text-slate-900">Relatórios</p>
              <p className="text-xs text-slate-500">Análises e dados</p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <DashboardLayout>
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-bold text-slate-900">Dashboard</h1>
          <p className="text-slate-600 mt-2">Bem-vindo, {user?.name}! 👋</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <Card className="border-0 shadow-sm hover:shadow-md transition-shadow">
            <CardHeader className="pb-3">
              <CardTitle className="text-sm font-medium text-slate-600 flex items-center gap-2">
                <Package className="w-4 h-4 text-emerald-600" />
                Medicamentos
              </CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-3xl font-bold text-slate-900">--</p>
              <p className="text-xs text-slate-500 mt-1">Produtos cadastrados</p>
            </CardContent>
          </Card>

          <Card className="border-0 shadow-sm hover:shadow-md transition-shadow">
            <CardHeader className="pb-3">
              <CardTitle className="text-sm font-medium text-slate-600 flex items-center gap-2">
                <ShoppingCart className="w-4 h-4 text-blue-600" />
                Vendas Hoje
              </CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-3xl font-bold text-slate-900">--</p>
              <p className="text-xs text-slate-500 mt-1">Transações realizadas</p>
            </CardContent>
          </Card>

          <Card className="border-0 shadow-sm hover:shadow-md transition-shadow">
            <CardHeader className="pb-3">
              <CardTitle className="text-sm font-medium text-slate-600 flex items-center gap-2">
                <Users className="w-4 h-4 text-purple-600" />
                Clientes
              </CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-3xl font-bold text-slate-900">--</p>
              <p className="text-xs text-slate-500 mt-1">Clientes cadastrados</p>
            </CardContent>
          </Card>

          <Card className="border-0 shadow-sm hover:shadow-md transition-shadow">
            <CardHeader className="pb-3">
              <CardTitle className="text-sm font-medium text-slate-600 flex items-center gap-2">
                <BarChart3 className="w-4 h-4 text-orange-600" />
                Faturamento
              </CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-3xl font-bold text-slate-900">R$ --</p>
              <p className="text-xs text-slate-500 mt-1">Total do mês</p>
            </CardContent>
          </Card>
        </div>

        <Card className="border-0 shadow-sm">
          <CardHeader>
            <CardTitle>Atalhos Rápidos</CardTitle>
            <CardDescription>Acesse rapidamente as principais funcionalidades</CardDescription>
          </CardHeader>
          <CardContent className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-3">
            <Button variant="outline" className="justify-start h-auto py-3 px-4">
              <Package className="w-5 h-5 mr-2 text-emerald-600" />
              <div className="text-left">
                <p className="font-medium text-sm">Catálogo</p>
                <p className="text-xs text-slate-500">Gerenciar medicamentos</p>
              </div>
            </Button>
            <Button variant="outline" className="justify-start h-auto py-3 px-4">
              <ShoppingCart className="w-5 h-5 mr-2 text-blue-600" />
              <div className="text-left">
                <p className="font-medium text-sm">Nova Venda</p>
                <p className="text-xs text-slate-500">Registrar venda</p>
              </div>
            </Button>
            <Button variant="outline" className="justify-start h-auto py-3 px-4">
              <Users className="w-5 h-5 mr-2 text-purple-600" />
              <div className="text-left">
                <p className="font-medium text-sm">Clientes</p>
                <p className="text-xs text-slate-500">Gerenciar clientes</p>
              </div>
            </Button>
            <Button variant="outline" className="justify-start h-auto py-3 px-4">
              <BarChart3 className="w-5 h-5 mr-2 text-orange-600" />
              <div className="text-left">
                <p className="font-medium text-sm">Relatórios</p>
                <p className="text-xs text-slate-500">Ver análises</p>
              </div>
            </Button>
          </CardContent>
        </Card>
      </div>
    </DashboardLayout>
  );
}
