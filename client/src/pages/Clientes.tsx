import DashboardLayout from "@/components/DashboardLayout";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { trpc } from "@/lib/trpc";
import { Plus, Search, Users } from "lucide-react";
import { useState } from "react";
import { toast } from "sonner";

const clienteSchema = z.object({
  cpf: z.string().optional(),
  nome: z.string().min(1, "Nome obrigatório"),
  email: z.string().email().optional().or(z.literal("")),
  telefone: z.string().optional(),
  endereco: z.string().optional(),
  cidade: z.string().optional(),
  estado: z.string().optional(),
  cep: z.string().optional(),
  dataNascimento: z.string().optional(),
  convenioMedico: z.string().optional(),
});

type ClienteForm = z.infer<typeof clienteSchema>;

export default function Clientes() {
  const [searchTerm, setSearchTerm] = useState("");
  const [openDialog, setOpenDialog] = useState(false);

  const clientesQuery = trpc.clientes.getClientes.useQuery();
  const createCliente = trpc.clientes.createCliente.useMutation({
    onSuccess: () => {
      toast.success("Cliente criado com sucesso!");
      clientesQuery.refetch();
      setOpenDialog(false);
      form.reset();
    },
    onError: (error) => {
      toast.error(`Erro: ${error.message}`);
    },
  });

  const form = useForm({
    resolver: zodResolver(clienteSchema),
    defaultValues: {
      cpf: "",
      nome: "",
      email: "",
      telefone: "",
      endereco: "",
      cidade: "",
      estado: "",
      cep: "",
      dataNascimento: "",
      convenioMedico: "",
    },
  });

  const onSubmit = (data: any) => {
    const clienteData: any = {
      ...data,
      email: data.email || undefined,
    };
    if (data.dataNascimento) {
      clienteData.dataNascimento = new Date(data.dataNascimento);
    }
    createCliente.mutate(clienteData);
  };

  const clientes = clientesQuery.data || [];
  const filteredClientes = clientes.filter(c =>
    c.nome.toLowerCase().includes(searchTerm.toLowerCase()) ||
    (c.cpf && c.cpf.includes(searchTerm))
  );

  const clientesIdosos = clientes.filter(c => c.ehIdoso);

  return (
    <DashboardLayout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold text-slate-900">Gestão de Clientes</h1>
            <p className="text-slate-600 mt-2">Cadastro e histórico de clientes</p>
          </div>
          <Dialog open={openDialog} onOpenChange={setOpenDialog}>
            <DialogTrigger asChild>
              <Button className="bg-gradient-to-r from-purple-500 to-pink-600 hover:from-purple-600 hover:to-pink-700">
                <Plus className="w-4 h-4 mr-2" />
                Novo Cliente
              </Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-md max-h-[90vh] overflow-y-auto">
              <DialogHeader>
                <DialogTitle>Adicionar Cliente</DialogTitle>
                <DialogDescription>Preencha os dados do novo cliente</DialogDescription>
              </DialogHeader>
              <Form {...form}>
                <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                  <FormField
                    control={form.control}
                    name="nome"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Nome Completo</FormLabel>
                        <FormControl>
                          <Input placeholder="Ex: João Silva" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name="cpf"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>CPF</FormLabel>
                        <FormControl>
                          <Input placeholder="000.000.000-00" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name="email"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Email</FormLabel>
                        <FormControl>
                          <Input type="email" placeholder="email@example.com" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name="telefone"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Telefone</FormLabel>
                        <FormControl>
                          <Input placeholder="(11) 99999-9999" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name="dataNascimento"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Data de Nascimento</FormLabel>
                        <FormControl>
                          <Input type="date" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name="convenioMedico"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel>Convênio Médico</FormLabel>
                        <FormControl>
                          <Input placeholder="Ex: Unimed" {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <Button type="submit" className="w-full" disabled={createCliente.isPending}>
                    {createCliente.isPending ? "Criando..." : "Criar Cliente"}
                  </Button>
                </form>
              </Form>
            </DialogContent>
          </Dialog>
        </div>

        {clientesIdosos.length > 0 && (
          <Card className="border-purple-200 bg-purple-50">
            <CardHeader className="pb-3">
              <CardTitle className="text-sm font-medium flex items-center gap-2 text-purple-900">
                <Users className="w-4 h-4" />
                Clientes Idosos
              </CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-sm text-purple-800">
                {clientesIdosos.length} cliente(s) idoso(s) com possibilidade de desconto especial
              </p>
            </CardContent>
          </Card>
        )}

        <Card className="border-0 shadow-sm">
          <CardHeader>
            <CardTitle>Clientes Cadastrados</CardTitle>
            <CardDescription>Total: {filteredClientes.length} clientes</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="mb-4">
              <div className="relative">
                <Search className="absolute left-3 top-3 w-4 h-4 text-slate-400" />
                <Input
                  placeholder="Buscar por nome ou CPF..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="pl-10"
                />
              </div>
            </div>

            {clientesQuery.isLoading ? (
              <div className="text-center py-8">
                <p className="text-slate-500">Carregando clientes...</p>
              </div>
            ) : filteredClientes.length === 0 ? (
              <div className="text-center py-12">
                <Users className="w-12 h-12 text-slate-300 mx-auto mb-3" />
                <p className="text-slate-500">Nenhum cliente encontrado</p>
              </div>
            ) : (
              <div className="space-y-3">
                {filteredClientes.map((cliente) => (
                  <Card key={cliente.id} className="border-slate-200">
                    <CardContent className="pt-6">
                      <div className="flex items-start justify-between">
                        <div className="flex-1">
                          <div className="flex items-center gap-2 mb-2">
                            <h3 className="font-semibold text-slate-900">{cliente.nome}</h3>
                            {cliente.ehIdoso && (
                              <Badge variant="secondary" className="bg-purple-100 text-purple-800">Idoso</Badge>
                            )}
                          </div>
                          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
                            {cliente.cpf && (
                              <div>
                                <p className="text-slate-500">CPF</p>
                                <p className="font-medium text-slate-900">{cliente.cpf}</p>
                              </div>
                            )}
                            {cliente.email && (
                              <div>
                                <p className="text-slate-500">Email</p>
                                <p className="font-medium text-slate-900">{cliente.email}</p>
                              </div>
                            )}
                            {cliente.telefone && (
                              <div>
                                <p className="text-slate-500">Telefone</p>
                                <p className="font-medium text-slate-900">{cliente.telefone}</p>
                              </div>
                            )}
                            {cliente.convenioMedico && (
                              <div>
                                <p className="text-slate-500">Convênio</p>
                                <p className="font-medium text-slate-900">{cliente.convenioMedico}</p>
                              </div>
                            )}
                          </div>
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </DashboardLayout>
  );
}
