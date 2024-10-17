import { z } from "zod";

import { EntrySchema, EntryTypeSchema } from "~/../prisma/generated/zod"; // All schemas are here by default, use the 'output' option to change it
import { createTRPCRouter, protectedProcedure } from "~/server/api/trpc";

import { SnowflakeId } from "@akashrajpurohit/snowflake-id";

const workerId = process.pid % 1024; // Using PID as workerId
const snowflake = SnowflakeId({ workerId: workerId, epoch: 1727733600 });

export const entryRouter = createTRPCRouter({
  create: protectedProcedure
    .input(
      EntrySchema.omit({
        hash: true,
        id: true,
        createdById: true,
        createdAt: true,
        watched: true,
        updatedAt: true,
      }),
    )
    .mutation(async ({ ctx, input }) => {
      return ctx.db.entry.create({
        data: {
          title: input.title,
          createdBy: { connect: { id: ctx.session.user.id } },
          hash: "", //TODO
          id: snowflake.generate(),
          type: input.type,
          parent: input.parentId ? {connect: {id: input.parentId}} : undefined,
          thumbnail: input.thumbnail,
          watched: false,
        },
      });
    }),

  getAll: protectedProcedure
    .input(
      z.object({
        count: z.number().optional(),
        type: EntryTypeSchema.optional(),
      }),
    )
    .query(async ({ ctx, input }) => {
      if (input.count && input.type)
        return ctx.db.entry.findMany({
          where: { type: input.type },
          take: input.count,
        });
      if (input.count)
        return ctx.db.entry.findMany({
          take: input.count,
        });
      if (input.type)
        return ctx.db.entry.findMany({
          where: { type: input.type },
        });
      return ctx.db.entry.findMany();
    }),

  getById: protectedProcedure
    .input(z.string())
    .query(async ({ ctx, input }) => {
      const entry = await ctx.db.entry.findUnique({
        where: { id: input },
      });

      return entry;
    }),

  getLatest: protectedProcedure.query(async ({ ctx }) => {
    const entry = await ctx.db.entry.findFirst({
      orderBy: { createdAt: "desc" },
      where: { createdBy: { id: ctx.session.user.id } },
    });

    return entry ?? null;
  }),

  getSecretMessage: protectedProcedure.query(() => {
    return "you can now see this secret message!";
  }),
});
